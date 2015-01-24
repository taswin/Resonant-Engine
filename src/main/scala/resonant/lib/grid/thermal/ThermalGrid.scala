package resonant.lib.grid.thermal

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.util.ForgeDirection
import resonant.api.IUpdate
import resonant.lib.grid.thermal.ThermalEvent.EventThermalUpdate
import resonant.lib.transform.vector.VectorWorld

/**
 * A grid managing the flow of thermal energy.
 *
 * Heat flows from hot to cold.
 */
object ThermalGrid extends IUpdate
{
  /**
   * A map of positions and heat source energy
   */
  private var heatMap = Map.empty[VectorWorld, Double].withDefaultValue(0d)

  /**
   * A map of temperature at every block position relative to its default temperature
   */
  private var deltaTemperatureMap = Map.empty[VectorWorld, Int].withDefaultValue(0)

  private var markClear = false

  override def update(deltaTime: Double)
  {
    heatMap synchronized
    {
      if (markClear)
      {
        heatMap = Map.empty
        deltaTemperatureMap = Map.empty
        markClear = false
      }

      println(heatMap.size)

      if (heatMap.size > 0)
        println("Min value: " + heatMap.values.min + " vs " + "Max value: " + heatMap.values.max)

      //There can't be negative energy, remove all heat values less than zero.
      heatMap --= heatMap.filter(_._2 <= 0).keySet

      heatMap.foreach
      {
        case (pos, heat) =>
        {
          /**
           * Heat is used to increase the kinetic energy of blocks, thereby increasing their temperature
           *
           * Specific Heat Capacity:
           * Q = mcT
           *
           * Therefore:
           * T = Q/mc
           */
          val specificHeatCapacity = ThermalPhysics.getSHC(pos.getBlock.getMaterial)
          val deltaTemperature = (heat / (1 * specificHeatCapacity)).toInt
          deltaTemperatureMap += pos -> deltaTemperature
        }
      }

      deltaTemperatureMap --= deltaTemperatureMap.filter(_._2 <= 0).keys

      heatMap.foreach
      {
        case (pos, heat) =>
        {
          val event = new EventThermalUpdate(pos, getTemperature(pos))

          MinecraftForge.EVENT_BUS.post(event)

          if (!event.isCanceled)
          {
            /**
             * Do heat transfer based on thermal conductivity
             *
             * Assume transfer by conduction
             *
             * Q = k * A * deltaTemp * deltaTime /d
             * Q = k * deltaTemp * deltaTime
             *
             * where k = thermal conductivity, A = 1 m^2, and d = 1 meter (for every block)
             */
            val temperature = getTemperature(pos)

            ForgeDirection.VALID_DIRECTIONS
              .map(pos + _)
              .foreach(
                adj =>
                {
                  val adjTemp = getTemperature(adj)

                  if (temperature > adjTemp)
                  {
                    //TODO: Based on materials
                    val thermalConductivity = 100 //2.18

                    val heatTransfer = Math.min(thermalConductivity * (temperature - adjTemp) * deltaTime, heat / 6)
                    addHeat(adj, heatTransfer)
                    removeHeat(pos, heatTransfer)
                  }
                }
              )
          }
        }
      }
    }
  }

  def addHeat(position: VectorWorld, heat: Double)
  {
    heatMap += position -> (heatMap(position) + heat)
  }

  def removeHeat(position: VectorWorld, heat: Double)
  {
    heatMap += position -> (heatMap(position) - heat)
  }

  /**
   * Gets the temperature at a specific position
   * @return - Temperature in Kelvin
   */
  def getTemperature(pos: VectorWorld): Int = ThermalPhysics.getDefaultTemperature(pos) + deltaTemperatureMap(pos)

  def clear()
  {
    markClear = true
  }

  override def updatePeriod = 50
}