package com.resonant.wrapper.core.content

import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fluids._
import nova.core.util.Direction

/**
 * Designed to debug fluid devices by draining everything that comes in at one time
 *
 * @author DarkGuardsman
 */
class TileInfiniteFluid extends TileIO(Material.iron) with IFluidHandler {
	var tank: FluidTank = new FluidTank(Integer.MAX_VALUE)
	var active: Boolean = false

	saveIOMap = true
	ioMap = 728

	override def update() {
		super.update

		if (active) {
			Direction.VALID_DIRECTIONS.filter(getOutputDirections.contains(_)).foreach(
				direction => {
					val tile: TileEntity = (position + direction).getTileEntity

					if (tile.isInstanceOf[IFluidHandler]) {
						(tile.asInstanceOf[IFluidHandler]).fill(direction.getOpposite, tank.getFluid, true)
					}

				});
		}
	}

	override def getTankInfo(from: Direction): Array[FluidTankInfo] = {
		return Array[FluidTankInfo](this.tank.getInfo)
	}

	override def fill(from: Direction, resource: FluidStack, doFill: Boolean): Int = {
		if (getInputDirections.contains(from)) {
			return resource.amount
		}
		return 0
	}

	override def drain(from: Direction, resource: FluidStack, doDrain: Boolean): FluidStack = {
		if (getOutputDirections.contains(from)) {
			return resource
		}
		return null
	}

	override def drain(from: Direction, maxDrain: Int, doDrain: Boolean): FluidStack = {
		if (getOutputDirections.contains(from)) {
			return this.tank.drain(maxDrain, false)
		}
		return null
	}

	override def canFill(from: Direction, fluid: Fluid): Boolean = getInputDirections.contains(from)

	override def canDrain(from: Direction, fluid: Fluid): Boolean = getOutputDirections.contains(from)

	/*
		def onActivated(entityPlayer: EntityPlayer): Boolean =
		{
		  if (entityPlayer != null && entityPlayer.getHeldItem != null)
		  {
			if (entityPlayer.getHeldItem.getItem eq Item.stick)
			{
			  this.active = !this.active
			  entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("[FluidVoid] Pumping:" + this.active))
			  return true
			}
			var stack: FluidStack = FluidContainerRegistry.getFluidForFilledItem(entityPlayer.getHeldItem)
			if (stack != null)
			{
			  stack = stack.copy
			  stack.amount = Integer.MAX_VALUE
			  this.tank.setFluid(stack)
			  entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("[FluidVoid] Fluid:" + stack.getFluid.getName))
			  return true
			}
		  }
		  return false
		}*/
}