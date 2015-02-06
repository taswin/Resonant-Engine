package resonantengine.prefab.block.impl

import nova.core.util.Direction
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

	def getEnergyStorage(from: Direction): EnergyStorage =
  {
    return energy
  }
}
