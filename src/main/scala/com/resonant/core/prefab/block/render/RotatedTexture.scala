package com.resonant.core.prefab.block.render

import com.resonant.core.prefab.block.Rotatable
import nova.core.block.Block
import nova.core.block.components.StaticRenderer
import nova.core.render.model.{BlockModelUtil, Model}
import nova.core.util.transform.MatrixStack

/**
 * Renders a block with rotation based on its direction
 * @author Calclavia
 */
trait RotatedTexture extends Block with Rotatable with StaticRenderer {

	override def renderStatic(model: Model) {
		BlockModelUtil.drawBlock(model, this)
		val stack = new MatrixStack()
		stack.loadMatrix(model.matrix)
		stack.rotate(direction.rotation)
		model.matrix = stack.getMatrix
	}
}
