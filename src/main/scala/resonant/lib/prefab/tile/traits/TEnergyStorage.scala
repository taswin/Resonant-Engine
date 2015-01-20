package resonant.lib.content.prefab

import net.minecraftforge.common.util.ForgeDirection
import resonant.lib.grid.energy.EnergyStorage

import scala.beans.BeanProperty

/**
 * Any object that can store energy
 * @author Calclavia
 */
trait TEnergyStorage
{
  @BeanProperty
  var energy: EnergyStorage = _

  /**
   * Sets the amount of energy this unit stored.
   *
   * This function is NOT recommended for calling.
   */
  def setEnergy(amount: Double, from: ForgeDirection) =
  {
    if (energy != null)
      energy.setEnergy(amount)
  }

  def getEnergy(from: ForgeDirection): Double =
  {
    if (energy != null)
    {
      return energy.getEnergy
    }
    return 0
  }

  def getEnergyCapacity(from: ForgeDirection): Double =
  {
    if (energy != null)
    {
      return energy.getEnergyCapacity
    }
    else
      return 0
  }
}
