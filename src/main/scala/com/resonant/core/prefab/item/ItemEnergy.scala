package com.resonant.core.prefab.item

import java.util
import java.util.Optional

import com.resonant.core.energy.EnergyItem
import com.resonant.wrapper.lib.utility.science.UnitDisplay
import nova.core.item.Item
import nova.core.player.Player
import nova.core.retention.{Storable, Stored}

/**
 * A trait implementation of IEnergyItem
 *
 * @author Calclavia
 */
trait ItemEnergy extends Item with EnergyItem with Storable {

	protected var maxEnergy = 0d

	@Stored
	protected var energy = 0d

	override def getMaxCount: Int = 1

	override def getTooltips(player: Optional[Player], tooltips: util.List[String]) {
		super.getTooltips(player, tooltips)

		val color = {
			if (energy <= maxEnergy / 3) {
				"\u00a74"
			}
			else if (energy > maxEnergy * 2 / 3) {
				"\u00a72"
			}
			else {
				"\u00a76"
			}
		}

		tooltips.add(color + new UnitDisplay(UnitDisplay.Unit.JOULES, energy) + "/" + new UnitDisplay(UnitDisplay.Unit.JOULES, maxEnergy).symbol)
	}

	override def recharge(energy: Double, doReceive: Boolean): Double = {
		val energyReceived: Double = Math.min(maxEnergy - energy, Math.min(getTransferRate, energy))
		if (doReceive) {
			setEnergy(energy + energyReceived)
		}
		return energyReceived
	}

	/**
	 * Makes sure the item is uncharged when it is crafted and not charged.

	override def onCreated(itemStack: ItemStack, par2World: World, par3EntityPlayer: EntityPlayer) {
		setEnergy(itemStack, 0)
	 */

	override def setEnergy(energy: Double) {
		this.energy = energy
	}

	def getTransferRate: Double = maxEnergy / 100

	override def getEnergy = energy

	def discharge(energy: Double, doTransfer: Boolean): Double = {
		val energyExtracted: Double = Math.min(energy, Math.min(getTransferRate, energy))
		if (doTransfer) {
			setEnergy(energy - energyExtracted)
		}
		return energyExtracted
	}

	def getTransfer(): Double = {
		return maxEnergy - energy
	}

	/*
		override def getSubItems(id: Item, par2CreativeTabs: CreativeTabs, par3List: List[_]) {
			par3List.add(Compatibility.getItemWithCharge(new ItemStack(this), 0))
			par3List.add(Compatibility.getItemWithCharge(new ItemStack(this), getEnergyCapacity(new ItemStack(this))))
		}*/
}