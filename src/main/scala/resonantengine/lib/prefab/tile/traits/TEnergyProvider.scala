package resonantengine.lib.prefab.tile.traits

import net.minecraftforge.common.util.ForgeDirection
import resonantengine.lib.grid.energy.EnergyStorage

import scala.beans.BeanProperty

/**
 * Any object that can store energy
 * @author Calclavia
 */
trait TEnergyProvider
{
  @BeanProperty
  var energy = new EnergyStorage

  def getEnergyStorage(from: ForgeDirection): EnergyStorage =
  {
    return energy
  }
}
