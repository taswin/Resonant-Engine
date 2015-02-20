package com.resonant.core.prefab.block

import java.util.Optional

import com.resonant.lib.util.RotationUtility
import com.resonant.wrapper.lib.wrapper.BitmaskWrapper._
import nova.core.block.Block
import nova.core.render.model.{BlockModelUtil, Model, StaticCubeTextureCoordinates}
import nova.core.render.texture.BlockTexture
import nova.core.util.Direction

/**
 * A trait for blocks with connected textures.
 */
trait BlockConnectedTexture extends Block {

	override def renderStatic(model: Model) {
		//Render the block face
		super.renderStatic(model)
		//Render the block edge
		val bounds = getBoundingBox
		for (dir <- Direction.DIRECTIONS; r <- 0 until 4) {
			if (!sideMask.mask(dir)) {
				val absDir = Direction.fromOrdinal(RotationUtility.rotateSide(dir.ordinal, r))

				if (!sideMask.mask(absDir)) {
					val face = BlockModelUtil.drawDir(absDir, model, bounds.min.x, bounds.min.y, bounds.min.z, bounds.max.x, bounds.max.y, bounds.max.z, StaticCubeTextureCoordinates.instance)
					face.texture = Optional.of(edgeTexture)
				}
			}
		}
	}

	def edgeTexture: BlockTexture

	def sideMask: Int
}