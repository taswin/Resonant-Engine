package resonant.lib.grid.thermal

import net.minecraftforge.common.util.ForgeDirection
import resonant.api.IUpdate
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
  private var deltaTemperatureMap = Map.empty[VectorWorld, Float].withDefaultValue(0f)

  def getDefaultTemperature(position: VectorWorld): Double = ThermalPhysics.getDefaultTemperature(position.world, position.xi, position.zi)

  override def update(deltaTime: Double)
  {
    heatMap synchronized
    {
      //There can't be negative energy, remove all heat values less than zero.
      heatMap --= heatMap.filter(_._2 <= 0).keys

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
          val specificHeatCapacity = 4200
          val deltaTemp = (heat / (1 * specificHeatCapacity)).toFloat
          addTemperature(pos, deltaTemp)

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
          val currentTemp = getTemperature(pos)

          ForgeDirection.VALID_DIRECTIONS
            .map(pos + _)
            .foreach(
              adj =>
              {
                //TODO: Based on materials
                val thermalConductivity = 2.18
                val adjTemp = getTemperature(adj)

                if (currentTemp > adjTemp)
                {
                  val heatTransfer = thermalConductivity * (currentTemp - adjTemp) * deltaTime
                  addHeat(adj, heatTransfer)
                  removeHeat(pos, heatTransfer)
                }
              }
            )
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
  def getTemperature(position: VectorWorld): Float = ThermalPhysics.getDefaultTemperature(position.world, position.xi, position.zi) + deltaTemperatureMap(position)

  private def addTemperature(pos: VectorWorld, temperature: Float): Unit =
  {
    deltaTemperatureMap += pos -> (deltaTemperatureMap(pos) + temperature)
  }

  override def updatePeriod = 50
}