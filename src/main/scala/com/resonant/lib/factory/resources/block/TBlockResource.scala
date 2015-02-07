package com.resonant.lib.factory.resources.block

import com.resonant.lib.factory.resources.ResourceFactory
import com.resonant.lib.modcontent.block.ResonantBlock
import cpw.mods.fml.relauncher.{Side, SideOnly}

/**
 * @author Calclavia
 */
trait TBlockResource extends ResonantBlock {
	var resourceMaterial: String = ""

	@SideOnly(Side.CLIENT)
	override def colorMultiplier: Int = ResourceFactory.getColor(resourceMaterial)
}
