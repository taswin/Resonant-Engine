package resonant.lib.grid.core

import java.util.concurrent.ConcurrentLinkedQueue

import cpw.mods.fml.common.eventhandler.{Event, SubscribeEvent}
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.common.MinecraftForge
import resonant.api.IUpdate

import scala.collection.convert.wrapAll._
import scala.collection.mutable

/**
 * A ticker to update all grids. This is multi-threaded based on configuration.
 *
 * @author Calclavia
 */
object UpdateTicker
{
  final val threaded = new UpdateTicker
  final val world = new UpdateTicker
}

class UpdateTicker extends Thread
{
  setName("Universal Electricity")
  setPriority(Thread.MIN_PRIORITY)

  /**
   * A map of all updaters and how many milliseconds has passed since the last update
   */
  private final val updaters = mutable.WeakHashMap.empty[IUpdate, Long]

  /**
   * A queue of functions that will be executed in the next update loop.
   */
  private final val queue = new ConcurrentLinkedQueue[() => Unit]()

  /**
   * Becomes true if the network needs to be paused.
   */
  var pause = false

  /**
   * The time in milliseconds between successive updates.
   */
  private var deltaTime = 0L

  private var shortestPeriod = 1000 / 20

  def addUpdater(updater: IUpdate)
  {
    enqueue(() =>
    {
      updaters += updater -> 0
    })
  }

  def enqueue(f: () => Unit)
  {
    queue.add(f)
  }

  def removeUpdater(updater: IUpdate)
  {
    enqueue(() =>
    {
      updaters -= updater
    })
  }

  def queueEvent(event: Event)
  {
    queue.add(() => MinecraftForge.EVENT_BUS.post(event))
  }

  def getUpdaterCount = updaters.size

  override def run()
  {
    var last = System.currentTimeMillis()

    while (true)
    {
      if (!pause)
      {
        val current = System.currentTimeMillis()
        deltaTime = current - last
        update()
        last = current
      }

      Thread.sleep(shortestPeriod)
    }
  }

  @SubscribeEvent
  def tickEnd(event: TickEvent.ServerTickEvent)
  {
    if (event.phase == Phase.END)
    {
      deltaTime = 50
      update()
    }
  }

  def update()
  {
    try
    {
      queue synchronized
      {
        queue.foreach(_.apply())
        queue.clear()
      }

      updaters synchronized
      {
        updaters --= updaters.keys.filter(_.updateRate <= 0)

        if (this == UpdateTicker.threaded)
        {
          updaters.foreach(keyVal => updaters(keyVal._1) = keyVal._2 + deltaTime)

          updaters.par
            .filter(keyVal => keyVal._2 >= 1000 / keyVal._1.updateRate())
            .foreach(
              keyVal =>
              {
                keyVal._1.update(getDeltaTime / 1000d)
                updaters(keyVal._1) = keyVal._2 % (1000 / keyVal._1.updateRate())
              }
            )

          shortestPeriod = updaters.keys.map(1000 / _.updateRate).min
        }
        else
        {
          updaters.keys.foreach(_.update(getDeltaTime / 1000d))
        }
      }
    }
    catch
      {
        case e: Exception =>
        {
          System.out.println("Universal Electricity Ticker: Failed while ticking updaters. This is a bug! Clearing all tickers for self repair.")
          updaters.clear()
          e.printStackTrace()
        }
      }
  }

  def getDeltaTime = deltaTime
}