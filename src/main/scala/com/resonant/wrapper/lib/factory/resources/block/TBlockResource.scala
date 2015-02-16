package com.resonant.wrapper.lib.factory.resources.block

import com.resonant.wrapper.lib.factory.resources.ResourceFactory
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block

/**
 * A block that retains a material
 * @author Calclavia
 */
trait TBlockResource extends Block {
	var resourceMaterial: String = ""

	@SideOnly(Side.CLIENT)
	override def colorMultiplier: Int = ResourceFactory.getColor(resourceMaterial)
}
