package resonant.lib.grid.energy.electric

import cofh.api.energy.IEnergyHandler
import net.minecraftforge.common.util.ForgeDirection
import resonant.lib.grid.core.TBlockNodeProvider
import resonant.lib.mod.compat.energy.Compatibility
import resonant.lib.prefab.tile.traits.TEnergyProvider

/**
 * An energy bridge between TE and UE
 * @author Calclavia
 */
trait TTEBridge extends TBlockNodeProvider with TEnergyProvider with IEnergyHandler
{
  val electricNode = new NodeElectricComponent(this)

  override def receiveEnergy(from: ForgeDirection, maxReceive: Int, simulate: Boolean): Int =
  {
    if (simulate)
      return (energy + (maxReceive / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
    else
      return (energy += (maxReceive / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
  }

  override def extractEnergy(from: ForgeDirection, maxExtract: Int, simulate: Boolean): Int =
  {
    if (simulate)
      return (energy + (maxExtract / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
    else
      return (energy += (maxExtract / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
  }

  override def getEnergyStored(from: ForgeDirection): Int =
  {
    return (energy.value / Compatibility.redstoneFluxRatio).asInstanceOf[Int]
  }

  override def getMaxEnergyStored(from: ForgeDirection): Int =
  {
    return (energy.value / Compatibility.redstoneFluxRatio).asInstanceOf[Int]
  }

  override def canConnectEnergy(from: ForgeDirection): Boolean =
  {
    return electricNode.canConnect(from)
  }
}
