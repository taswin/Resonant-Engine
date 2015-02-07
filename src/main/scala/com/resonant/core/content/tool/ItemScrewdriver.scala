package com.resonant.core.content.tool

import java.util.List

import buildcraft.api.tools.IToolWrench
import com.resonant.lib.utility.LanguageUtility
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import net.minecraft.world.World
import nova.core.item.Item

class ItemScrewdriver extends Item with IToolWrench {

	override def getID: String = "screwdriver"

	override def canWrench(entityPlayer: EntityPlayer, x: Int, y: Int, z: Int): Boolean = true

	override def wrenchUsed(entityPlayer: EntityPlayer, x: Int, y: Int, z: Int) {
	}

	def addInformation(itemStack: ItemStack, par2EntityPlayer: EntityPlayer, par3List: List[_], par4: Boolean) {
		par3List.add(LanguageUtility.getLocal("toolmode.mode") + ": " + LanguageUtility.getLocal(ToolMode.REGISTRY.get(getMode(itemStack)).getName))
		par3List.addAll(LanguageUtility.splitStringPerWord(LanguageUtility.getLocal("item.resonant:screwdriver.tooltip"), 4))
	}

	def getMode(itemStack: ItemStack): Int = {
		return NBTUtility.getNBTTagCompound(itemStack).getInteger("mode")
	}

	def onItemRightClick(itemStack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
		if (player.isSneaking) {
			setMode(itemStack, (getMode(itemStack) + 1) % ToolMode.REGISTRY.size)
			if (!world.isRemote) {
				player.addChatMessage(new
						ChatComponentText(LanguageUtility.getLocal("item.resonant:screwdriver.toolmode.set") + LanguageUtility.getLocal(ToolMode.REGISTRY.get(getMode(itemStack)).getName)))
			}
			return itemStack
		}
		return ToolMode.REGISTRY.get(getMode(itemStack)).onItemRightClick(itemStack, world, player)
	}

	def setMode(itemStack: ItemStack, mode: Int) {
		NBTUtility.getNBTTagCompound(itemStack).setInteger("mode", mode)
	}

	def onItemUseFirst(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		return ToolMode.REGISTRY.get(getMode(stack)).onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
	}

	def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		return ToolMode.REGISTRY.get(getMode(stack)).onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
	}

	def doesSneakBypassUse(world: World, x: Int, y: Int, z: Int, player: EntityPlayer): Boolean = {
		return true
	}
}