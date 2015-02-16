package com.resonant.wrapper.lib.schematic

import java.util

import nova.core.block.Block
import nova.core.util.components.Storable
import nova.core.util.transform.{MatrixStack, Quaternion, Vector3d, Vector3i}

/**
 * Custom structure based on stored state.
 * @author Calclavia
 */
class StructureCustom(val name: String) extends Structure with Storable {

	/**
	 * A map of unit vector to block positions.
	 */
	var structure = Map.empty[Vector3d, Block]

	override def getStructure: Set[Vector3i] = {
		return getBlockStructure.keySet
	}

	override def getBlockStructure: Map[Vector3i, Block] = {
		val matrix = new MatrixStack().translate(translate).scale(scale).rotate(rotation).getMatrix
		return structure.map(e => (e._1.transform(matrix).toInt, e._2))
	}

	override def load(data: util.Map[String, AnyRef]) {
		translate = data.get("translate").asInstanceOf[Vector3d]
		scale = data.get("scale").asInstanceOf[Vector3d]
		rotation = data.get("rotation").asInstanceOf[Quaternion]
		structure = data.get("structure").asInstanceOf[Map[Vector3d, Block]]
	}

	override def save(data: util.Map[String, AnyRef]) {
		data.put("translate", translate)
		data.put("scale", scale)
		data.put("rotation", rotation)
		data.put("structure", structure.asInstanceOf[util.Map[String, AnyRef]])
	}

	/**
	 * Gets the equation that define the 3D surface in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def surfaceEquation(position: Vector3d): Double = Double.PositiveInfinity

	/**
	 * Gets the equation that define the 3D volume in standard form.
	 * The transformation should be default.
	 * @return The result of the equation. Zero if the position satisfy the equation.
	 */
	override def intersects(position: Vector3d): Double = Double.PositiveInfinity

	override def getID: String = name
}
