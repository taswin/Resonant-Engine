package com.resonant.prefab.item

import java.util.List

import com.resonant.lib.mod.compat.energy.Compatibility
import com.resonant.lib.utility.science.UnitDisplay
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

/**
 * A trait implementation of IEnergyItem
 *
 * @author Calclavia
 */
trait TEnergyItem extends Item with IEnergyItem {
	protected var nbtName: String = "energy"

	setMaxStackSize(1)
	setMaxDamage(100)
	setNoRepair()

	override def addInformation(itemStack: ItemStack, entityPlayer: EntityPlayer, list: List[_], par4: Boolean) {
		val energy = getEnergy(itemStack)
		val color = {
			if (energy <= getEnergyCapacity(itemStack) / 3) {
				"\u00a74"
			}
			else if (energy > getEnergyCapacity(itemStack) * 2 / 3) {
				"\u00a72"
			}
			else {
				"\u00a76"
			}
		}

		list.add(color + new UnitDisplay(UnitDisplay.Unit.JOULES, energy) + "/" + new UnitDisplay(UnitDisplay.Unit.JOULES, getEnergyCapacity(itemStack)).symbol)
	}

	/**
	 * Makes sure the item is uncharged when it is crafted and not charged.
	 */
	override def onCreated(itemStack: ItemStack, par2World: World, par3EntityPlayer: EntityPlayer) {
		setEnergy(itemStack, 0)
	}

	override def setEnergy(itemStack: ItemStack, energy: Double): ItemStack = {
		if (itemStack.getTagCompound == null) {
			itemStack.setTagCompound(new NBTTagCompound)
		}
		val electricityStored = Math.max(Math.min(energy, getEnergyCapacity(itemStack)), 0)
		itemStack.getTagCompound.setDouble(nbtName, electricityStored)
		itemStack.setItemDamage((100 - (electricityStored / getEnergyCapacity(itemStack)) * 100).toInt)
		return itemStack
	}

	override def recharge(itemStack: ItemStack, energy: Double, doReceive: Boolean): Double = {
		val energyReceived: Double = Math.min(getEnergyCapacity(itemStack) - getEnergy(itemStack), Math.min(getTransferRate(itemStack), energy))
		if (doReceive) {
			setEnergy(itemStack, getEnergy(itemStack) + energyReceived)
		}
		return energyReceived
	}

	def discharge(itemStack: ItemStack, energy: Double, doTransfer: Boolean): Double = {
		val energyExtracted: Double = Math.min(getEnergy(itemStack), Math.min(getTransferRate(itemStack), energy))
		if (doTransfer) {
			setEnergy(itemStack, getEnergy(itemStack) - energyExtracted)
		}
		return energyExtracted
	}

	def getTransferRate(itemStack: ItemStack): Double = {
		return getEnergyCapacity(itemStack) / 100
	}

	override def getEnergy(itemStack: ItemStack): Double = {
		if (itemStack.getTagCompound == null) {
			itemStack.setTagCompound(new NBTTagCompound)
		}
		val energyStored = itemStack.getTagCompound.getDouble(nbtName)
		itemStack.setItemDamage((100 - (energyStored.toDouble / getEnergyCapacity(itemStack)) * 100).toInt)
		return energyStored
	}

	def getTransfer(itemStack: ItemStack): Double = {
		return getEnergyCapacity(itemStack) - getEnergy(itemStack)
	}

	override def getSubItems(id: Item, par2CreativeTabs: CreativeTabs, par3List: List[_]) {
		par3List.add(Compatibility.getItemWithCharge(new ItemStack(this), 0))
		par3List.add(Compatibility.getItemWithCharge(new ItemStack(this), getEnergyCapacity(new ItemStack(this))))
	}
}