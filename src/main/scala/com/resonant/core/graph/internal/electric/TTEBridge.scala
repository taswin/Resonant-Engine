package com.resonant.core.graph.internal.electric

import cofh.api.energy.IEnergyHandler
import com.resonant.core.energy.EnergyStorage
import com.resonant.core.graph.api.NodeProvider
import com.resonant.wrapper.lib.compat.energy.Compatibility
import net.minecraftforge.common.util.ForgeDirection
import nova.core.util.Direction

/**
 * An energy bridge between TE and UE
 * @author Calclavia
 */
trait TTEBridge extends NodeProvider with IEnergyHandler {

	val electricNode = new NodeElectricComponent(this)
	val energy: EnergyStorage

	override def receiveEnergy(from: ForgeDirection, maxReceive: Int, simulate: Boolean): Int = {
		if (simulate) {
			return (energy + (maxReceive / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
		else {
			return (energy += (maxReceive / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
	}

	override def extractEnergy(from: ForgeDirection, maxExtract: Int, simulate: Boolean): Int = {
		if (simulate) {
			return (energy + (maxExtract / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
		else {
			return (energy += (maxExtract / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
	}

	override def getEnergyStored(from: ForgeDirection): Int = {
		return (energy.value / Compatibility.redstoneFluxRatio).asInstanceOf[Int]
	}

	override def getMaxEnergyStored(from: ForgeDirection): Int = {
		return (energy.value / Compatibility.redstoneFluxRatio).asInstanceOf[Int]
	}

	override def canConnectEnergy(from: ForgeDirection): Boolean = {
		return electricNode.canConnect(null, Direction.fromOrdinal(from.ordinal()))
	}
}
