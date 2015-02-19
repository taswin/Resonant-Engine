package com.resonant.wrapper.lib.factory.resources.block

import java.awt.Color

import com.resonant.wrapper.lib.factory.resources.Resource
import nova.core.block.Block
import nova.core.util.transform.Vector3d

/**
 * A generic ore block that is automatically colored/textured based on a color multiplier.
 * @author Calclavia
 */
class TileOre extends Block with Resource {
	var renderingForeground = false

	//TODO: Register these textures:
	//"oreForeground"
	//"oreBackground"

	override def colorMultiplier: Int = 0xFFFFFF

	@SideOnly(Side.CLIENT)
	override def getIcon: IIcon = if (renderingForeground) Block.icon.get("oreForeground") else super.getIcon

	@SideOnly(Side.CLIENT)
	override def renderInventory(itemStack: ItemStack) {
		renderingForeground = false
		//Render background
		GL11.glPushMatrix()
		RenderUtility.renderNormalBlockAsItem(itemStack.getItem.asInstanceOf[ItemBlock].field_150939_a, itemStack.getItemDamage, RenderUtility.renderBlocks)
		GL11.glPopMatrix()

		renderingForeground = true
		//Render foreground
		GL11.glPushMatrix()
		val material = ResourceFactory.getMaterial(itemStack.getItem.asInstanceOf[ItemBlock].field_150939_a)
		val color = new Color(ResourceFactory.getColor(material))
		GL11.glColor4f(color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f, 1)
		RenderUtility.renderBlocks.setOverrideBlockTexture(Block.icon.get("oreForeground"))
		RenderUtility.renderNormalBlockAsItem(itemStack.getItem.asInstanceOf[ItemBlock].field_150939_a, itemStack.getItemDamage, RenderUtility.renderBlocks)
		RenderUtility.renderBlocks.clearOverrideBlockTexture()
		GL11.glPopMatrix()
	}

	@SideOnly(Side.CLIENT)
	override def renderStatic(renderer: RenderBlocks, pos: Vector3d, pass: Int): Boolean = {
		renderingForeground = false
		//Render background
		renderer.renderStandardBlock(block, pos.xi, pos.yi, pos.zi)

		renderingForeground = true
		//Render foreground
		val color = new Color(ResourceFactory.getColor(resourceMaterial))

		if (Minecraft.isAmbientOcclusionEnabled && block.getLightValue() == 0) {
			if (renderer.partialRenderBounds) {
				renderer.renderStandardBlockWithAmbientOcclusionPartial(block, pos.xi, pos.yi, pos.zi, color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f)
			}
			else {
				renderer.renderStandardBlockWithAmbientOcclusion(block, pos.xi, pos.yi, pos.zi, color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f)
			}
		}
		else {
			renderer.renderStandardBlockWithColorMultiplier(block, pos.xi, pos.yi, pos.zi, color.getRed / 255f, color.getGreen / 255f, color.getBlue / 255f)
		}
		return true
	}
}
