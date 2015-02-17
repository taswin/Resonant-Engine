package com.resonant.wrapper.core.debug

import com.resonant.wrapper.core.api.tile.DebugInfo
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent

/**
 * A handler that allows GUI display on the F3 GUI
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
object F3Handler {
	@SubscribeEvent
	def onDrawDebugText(event: RenderGameOverlayEvent.Text) {
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			val player = Minecraft.getMinecraft.thePlayer
			val dim = player.worldObj.provider.dimensionId

			//TODO: Check if this will crash on multiplayer
			val world = if (FMLCommonHandler.instance.getSidedDelegate.getServer == null) Minecraft.getMinecraft().theWorld else FMLCommonHandler.instance().getMinecraftServerInstance.worldServerForDimension(dim)
			val objectPosition = player.rayTrace(8, 1)

			if (objectPosition != null) {
				val tile = world.getTileEntity(objectPosition.blockX, objectPosition.blockY, objectPosition.blockZ)

				if (tile.isInstanceOf[DebugInfo]) {
					event.left.addAll(tile.asInstanceOf[DebugInfo].getDebugInfo)
				}
			}
		}
	}

}
