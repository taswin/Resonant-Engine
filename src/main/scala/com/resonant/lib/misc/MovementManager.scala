package com.resonant.lib.misc

import nova.core.block.Block
import nova.core.retention.Data
import nova.core.util.transform.Vector3i
import nova.core.world.World

/**
 * @author Calclavia
 */
abstract class MovementManager {

	/**
	 * Sets a block in a sneaky manner, without notifying any systems.
	 * @param world
	 * @param pos
	 * @param data Optionally, data can be injected into the block.
	 */
	def setSneaky(world: World, pos: Vector3i, block: Block, data: Data = null)
}
