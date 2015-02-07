package com.resonant.graph.energy.electric

import cofh.api.energy.IEnergyHandler
import nova.core.util.Direction

/**
 * An energy bridge between TE and UE
 * @author Calclavia
 */
trait TTEBridge extends TBlockNodeProvider with TEnergyProvider with IEnergyHandler {
	val electricNode = new NodeElectricComponent(this)

	override def receiveEnergy(from: Direction, maxReceive: Int, simulate: Boolean): Int = {
		if (simulate) {
			return (energy + (maxReceive / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
		else {
			return (energy += (maxReceive / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
	}

	override def extractEnergy(from: Direction, maxExtract: Int, simulate: Boolean): Int = {
		if (simulate) {
			return (energy + (maxExtract / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
		else {
			return (energy += (maxExtract / Compatibility.redstoneFluxRatio) * Compatibility.redstoneFluxRatio).asInstanceOf[Int]
		}
	}

	override def getEnergyStored(from: Direction): Int = {
		return (energy.value / Compatibility.redstoneFluxRatio).asInstanceOf[Int]
	}

	override def getMaxEnergyStored(from: Direction): Int = {
		return (energy.value / Compatibility.redstoneFluxRatio).asInstanceOf[Int]
	}

	override def canConnectEnergy(from: Direction): Boolean = {
		return electricNode.canConnect(from)
	}
}
