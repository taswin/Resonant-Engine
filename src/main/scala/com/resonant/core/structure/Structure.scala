package com.resonant.core.structure

import java.util.Optional

import com.google.common.math.DoubleMath
import nova.core.block.Block
import nova.core.util.Identifiable
import nova.core.util.transform.{MatrixStack, Quaternion, Vector3d, Vector3i}

import scala.beans.BeanProperty

/**
 * Defines a 3D structure.
 * @author Calclavia
 */
abstract class Structure extends Identifiable {

	//The error allowed in fuzzy comparisons
	@BeanProperty
	var error = 0.001
	@BeanProperty
	var translate = Vector3d.zero
	@BeanProperty
	var scale = Vector3d.one
	@BeanProperty
	var rotation = Quaternion.identity
	@BeanProperty
	var block = Optional.empty[Block]()

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
			.filter(v => DoubleMath.fuzzyEquals(surfaceEquation(v.transform(rotationMatrix).divide(scale)), 0, error))
			.map(_ + translate)
			.map(_.toInt)
			.seq
			.toSet
	}

	def getBlockStructure: Map[Vector3i, Block] = {
		return getStructure
			.filter(getBlock(_).isPresent)
			.map(v => (v, getBlock(v).get()))
			.toMap
	}

	def getInteriorStructure: Set[Vector3i] = {
		//TODO: Use inverse matrix
		val rotationMatrix = new MatrixStack().rotate(rotation).getMatrix

		/**
		 * The equation has default transformations.
		 * Therefore, we need to transform the test vector back into the default, to test against the equation
		 */
		return searchSpace.par
			.filter(v => DoubleMath.fuzzyEquals(volumeEquation(v.transform(rotationMatrix).divide(scale)), 0, error))
			.map(_ + translate)
			.map(_.toInt)
			.seq
			.toSet
	}

	/**
	 * Gets the block at this position (relatively) 
	 * @param position
	 * @return
	 */
	def getBlock(position: Vector3i): Optional[Block] = block

	/**
	 * Checks if this world position is within this structure. 
	 * @param position The world position
	 * @return True if there is an intersection
	 */
	def intersects(position: Vector3d): Boolean = {
		//TODO: Use inverse matrix
		val rotationMatrix = new MatrixStack().rotate(rotation).getMatrix
		return DoubleMath.fuzzyEquals(volumeEquation((position - translate).transform(rotationMatrix).divide(scale)), 0, error)
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
	def volumeEquation(position: Vector3d): Double
}