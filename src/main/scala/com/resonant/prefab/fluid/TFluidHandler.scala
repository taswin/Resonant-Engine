package com.resonant.prefab.fluid

import net.minecraftforge.fluids._
import nova.core.util.Direction

/**
 * Prefab Trait for IFluidHandler
 * @author Calclavia
 */
trait TFluidHandler extends IFluidHandler with TTankProvider {
	override def fill(from: Direction, resource: FluidStack, doFill: Boolean): Int = {
		if (getTank != null) {
			return getTank.fill(resource, doFill)
		}
		else {
			return 0
		}
	}

	override def drain(from: Direction, resource: FluidStack, doDrain: Boolean): FluidStack = {
		if (getTank != null && resource != null && resource.isFluidEqual(getTank.getFluid)) {
			return drain(from, resource.amount, doDrain)
		}
		return null
	}

	override def drain(from: Direction, maxDrain: Int, doDrain: Boolean): FluidStack = {
		if (getTank != null) {
			return getTank.drain(maxDrain, doDrain)
		}
		else {
			return null
		}
	}

	override def canFill(from: Direction, fluid: Fluid): Boolean = true

	override def canDrain(from: Direction, fluid: Fluid): Boolean = true

	override def getTankInfo(from: Direction): Array[FluidTankInfo] = {
		if (getTank != null) {
			return Array[FluidTankInfo](getTank.getInfo)
		}
		else {
			return Array[FluidTankInfo]()
		}
	}
}
