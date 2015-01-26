package resonantengine.lib.grid.core

import java.util.concurrent.ConcurrentLinkedQueue

import cpw.mods.fml.common.eventhandler.{Event, SubscribeEvent}
import cpw.mods.fml.common.gameevent.TickEvent
import cpw.mods.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.common.MinecraftForge
import resonantengine.api.graph.IUpdate

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
   * Becomes true if the grid needs to be paused.
   */
  //TODO: Make this pause when "escape" is used
  var pause = false

  /**
   * The time in milliseconds between successive updates.
   */
  private var deltaTime = 0L

  private var shortestPeriod = 50

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
        updaters --= updaters.keys.filter(_.updatePeriod <= 0)

        if (this == UpdateTicker.threaded)
        {
          updaters.foreach(keyVal => updaters(keyVal._1) = keyVal._2 + deltaTime)

          updaters
            .filter(keyVal => keyVal._1.updatePeriod > 0 && keyVal._2 >= keyVal._1.updatePeriod)
            .foreach(
              keyVal =>
              {
                keyVal._1.update(getDeltaTime / 1000d)

                if (keyVal._1.updatePeriod > 0)
                  updaters(keyVal._1) = keyVal._2 % keyVal._1.updatePeriod
              }
            )

          shortestPeriod = updaters.keys.filter(_.updatePeriod > 0).map(_.updatePeriod).min
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

  @SubscribeEvent
  def tickEnd(event: TickEvent.ServerTickEvent)
  {
    if (event.phase == Phase.END)
    {
      deltaTime = 50
      update()
    }
  }
}
