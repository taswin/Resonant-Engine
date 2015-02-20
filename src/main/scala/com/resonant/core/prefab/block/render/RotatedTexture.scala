package com.resonant.core.prefab.block.render

import com.resonant.core.prefab.block.Rotatable
import nova.core.block.Block
import nova.core.render.model.Model
import nova.core.util.transform.MatrixStack

/**
 * Renders a block with rotation based on its direction
 * @author Calclavia
 */
trait RotatedTexture extends Block with Rotatable {

	override def renderStatic(model: Model) {
		super.renderStatic(model)
		val stack = new MatrixStack()
		stack.loadMatrix(model.matrix)
		stack.rotate(direction.rotation)
		model.matrix = stack.getMatrix
	}
}
