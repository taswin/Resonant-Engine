package com.resonant.wrapper.lib.schematic

import nova.core.block.Block
import nova.core.util.transform.Vector3d

/**
 * Creates a flat plane surface
 */
class StructurePlane(name: String, block: Block) extends Structure {

	/**
	 * Gets the equation that define the 3D volume in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def intersects(position: Vector3d): Double = surfaceEquation(position)

	/**
	 * Gets the equation that define the 3D surface in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def surfaceEquation(position: Vector3d): Double = position.x + position.y + position.z

	override def getID: String = "plane"
}