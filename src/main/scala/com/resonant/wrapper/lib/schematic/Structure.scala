package com.resonant.wrapper.lib.schematic

import com.google.common.math.DoubleMath
import nova.core.block.Block
import nova.core.util.Identifiable
import nova.core.util.transform.{MatrixStack, Quaternion, Vector3d, Vector3i}

/**
 * Defines a 3D structure.
 * @author Calclavia
 */
abstract class Structure(translate: Vector3d, scale: Vector3d, rotation: Quaternion) extends Identifiable {

	/**
	 * Do a search within an appropriate region by generating a search set.
	 */
	def searchSpace: Set[Vector3d] = {
		var search = Set.empty[Vector3d]

		for (x <- -scale.x / 2 to scale.x / 2 by 0.5; y <- -scale.y / 2 to scale.y / 2 by 0.5; z <- -scale.z / 2 to scale.z / 2 by 0.5) {
			search += new Vector3d(x, y, z)
		}
		return search
	}

	def getStructure: Set[Vector3i] = {
		//TODO: Use inverse matrix
		val rotationMatrix = new MatrixStack().rotate(rotation).getMatrix

		/**
		 * The equation has default transformations.
		 * Therefore, we need to transform the test vector back into the default, to test against the equation
		 */
		return searchSpace.par
			.filter(v => DoubleMath.fuzzyEquals(surfaceEquation(v.transform(rotationMatrix).divide(scale)), 0, 0.001))
			.map(_ + translate)
			.map(_.toInt)
			.seq
			.toSet
	}

	def getBlockStructure: Map[Vector3i, Block]

	/**
	 * Checks if this world position is within this structure. 
	 * @param position The world structure
	 * @return True if there is an intersection
	 */
	def isIntersects(position: Vector3d): Boolean = {
		val rotationMatrix = new MatrixStack().rotate(rotation).getMatrix
		return intersects((position - translate).transform(rotationMatrix).divide(scale))
	}

	/**
	 * Gets the equation that define the 3D surface in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	def surfaceEquation(position: Vector3d): Double

	/**
	 * Gets the equation that define the 3D volume in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	def intersects(position: Vector3d): Boolean
}