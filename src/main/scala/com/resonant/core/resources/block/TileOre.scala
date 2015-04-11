package com.resonant.core.resources.block

import java.util.Optional

import com.resonant.core.resources.{Resource, ResourceFactory}
import nova.core.block.Block
import nova.core.block.components.Modelled
import nova.core.render.Color
import nova.core.render.model.{BlockModelUtil, Model}
import nova.core.render.texture.Texture
import nova.core.util.Direction

/**
 * A generic ore block that is automatically colored/textured based on a color multiplier.
 * @author Calclavia
 */
class TileOre extends Block with Resource with Modelled {
	var renderingForeground = false

	override def colorMultiplier(side: Direction): Color = if (renderingForeground) Color.argb(ResourceFactory.getColor(material)) else Color.white

	override def getTexture(side: Direction): Optional[Texture] = if (renderingForeground) Optional.of(ResourceFactory.oreForeground) else Optional.of(ResourceFactory.oreBackground)

	override def renderStatic(model: Model) {
		renderingForeground = false
		BlockModelUtil.drawBlock(model, this)
		renderingForeground = true
		BlockModelUtil.drawBlock(model, this)
	}
}
