package com.resonant.lib.schematic

import net.minecraft.block.Block
import nova.core.util.Direction
import nova.core.util.collection.Pair
import nova.core.util.transform.Vector3d

/**
 * Used to store and generate schematic data for world generating buildings
 *
 * @param name Registry and unlocalized name "schematic name"
 * @author Calclavia, Darkguardsman
 */
abstract class Schematic(val name: String) {

	/**
	 * Generates generic placement data to be iterated threw to create the actual object in the world.
	 *
	 * @param dir - facing direction, optional
	 * @param size - requested size, optional
	 * @return map of locations to placement data
	 */
	def getStructure(dir: Direction, size: Int): Map[Vector3d, Pair[Block, Integer]]

	/**
	 * Creates a map of vectors in the shape of a line
	 *
	 * @param start - starting point of the line
	 * @param dir - direction to create it in
	 * @param block - block to make the line out of
	 * @param meta - - meta value of the block for placement
	 * @param length - length of the line
	 * @return HashMap of vectors to placement data
	 */
	def getLine(start: Vector3d, dir: Direction, block: Block, meta: Int, length: Int): Map[Vector3d, Pair[Block, Integer]] = {
		var returnMap = Map.empty[Vector3d, Pair[Block, Integer]]

		for (i <- 0 until length) {
			{
				returnMap += (dir.toVector.toDouble * i + start -> new Pair(block, meta))
			}
		}

		return returnMap
	}

	/**
	 * Creates a map of vectors in the shape of a square
	 *
	 * @param center - center to create the box around, controls offset for later if needed
	 * @param block - block to make the box out of
	 * @param meta - meta value of the block for placement
	 * @param size - size from the center to the edge, half of the side
	 * @return hash map of vectors to placement data
	 */
	def getBox(center: Vector3d, block: Block, meta: Int, size: Int): Map[Vector3d, Pair[Block, Integer]] = getBox(center, block, meta, size, size)

	/**
	 * Creates a map of vectors in the shape of a square
	 *
	 * @param center - center to create the box around, controls offset for later if needed
	 * @param block - block to make the box out of
	 * @param meta - meta value of the block for placement
	 * @param sizeX - size from the center to the edge, half of the side
	 * @param sizeZ - size from the center to the edge, half of the side
	 * @return hash map of vectors to placement data
	 */
	def getBox(center: Vector3d, block: Block, meta: Int, sizeX: Int, sizeZ: Int): Map[Vector3d, Pair[Block, Integer]] = {
		var returnMap = Map.empty[Vector3d, Pair[Block, Integer]]
		val start: Vector3d = new Vector3d(-sizeX, 0, -sizeZ) + center
		if (sizeX != sizeZ) {

			for (x <- 0 until sizeX * 2) {
				returnMap += (new Vector3d(x, 0, 0) + start -> new Pair[Block, Integer](block, meta))
				returnMap += (new Vector3d(x, 0, sizeZ * 2) + start -> new Pair[Block, Integer](block, meta))
			}

			for (z <- 0 until sizeZ * 2) {
				returnMap += (new Vector3d(0, 0, z) + start -> new Pair[Block, Integer](block, meta))
				returnMap += (new Vector3d(sizeX * 2, 0, z) + start -> new Pair[Block, Integer](block, meta))
			}
		}
		else {
			for (s <- 0 until sizeX * 2) {
				returnMap += (new Vector3d(s, 0, 0) + start -> new Pair[Block, Integer](block, meta))
				returnMap += (new Vector3d(s, 0, sizeZ * 2) + start -> new Pair[Block, Integer](block, meta))
				returnMap += (new Vector3d(0, 0, s) + start -> new Pair[Block, Integer](block, meta))
				returnMap += (new Vector3d(sizeZ * 2, 0, s) + start -> new Pair[Block, Integer](block, meta))
			}
		}
		return returnMap
	}
}