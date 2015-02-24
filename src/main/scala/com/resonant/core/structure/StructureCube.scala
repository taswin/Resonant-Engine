package com.resonant.core.structure

import nova.core.util.transform.{Cuboid, Vector3d}

/**
 * A cube structure.
 * @author Calclavia
 */
class StructureCube extends Structure {

	/**
	 * Gets the equation that define the 3D structure's surface.
	 */
	override def surfaceEquation(position: Vector3d): Double = Math.max(Math.abs(position.x), Math.max(Math.abs(position.y), Math.abs(position.z))) - 0.5

	/**
	 * Gets the equation that define the 3D volume in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def volumeEquation(position: Vector3d): Double = if (new Cuboid(new Vector3d(-0.5, -0.5, -0.5), new Vector3d(0.5, 0.5, 0.5)).intersects(position)) 0 else 1

	override def getID: String = "Cube"
}
