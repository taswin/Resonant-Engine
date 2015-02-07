package com.resonant.lib.schematic

import java.util.HashMap

import nova.core.util.collection.Pair
import nova.core.util.transform.Vector3d

/**
 * Creates a flat box shaped world gen
 *
 * @param name - unlocalized name for translation
 * @param block - block to build with
 */
class SchematicPlate(name: String, block: Block) extends Schematic {
	override def getName: String = name

	def getStructure(dir: Direction, size: Int): HashMap[Vector3d, Pair[Block, Integer]] = {
		val returnMap = new HashMap[Vector3d, Pair[Block, Integer]]

		for (x <- -size to size; y <- -size to size; z <- -size to size) {
			if ((dir.offsetX != 0 && x == 0) || (dir.offsetY != 0 && y == 0) || (dir.offsetZ != 0 && z == 0)) {
				returnMap.put(new Vector3d(x, y, z), new Pair[Block, Integer](block, dir.ordinal))
			}
		}

		return returnMap
	}
}