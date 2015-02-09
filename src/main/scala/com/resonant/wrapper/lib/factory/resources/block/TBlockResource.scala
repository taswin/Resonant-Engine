package com.resonant.wrapper.lib.factory.resources.block

import com.resonant.wrapper.lib.factory.resources.ResourceFactory
import cpw.mods.fml.relauncher.{Side, SideOnly}

/**
 * @author Calclavia
 */
trait TBlockResource extends ResonantBlock {
	var resourceMaterial: String = ""

	@SideOnly(Side.CLIENT)
	override def colorMultiplier: Int = ResourceFactory.getColor(resourceMaterial)
}
