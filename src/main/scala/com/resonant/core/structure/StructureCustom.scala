package com.resonant.core.structure

import java.util

import nova.core.block.Block
import nova.core.game.Game
import nova.core.retention.{Data, Storable}
import nova.core.util.transform.{MatrixStack, Vector3d, Vector3i}

import scala.collection.convert.wrapAll._

/**
 * Custom structure based on stored state.
 * @author Calclavia
 */
class StructureCustom(val name: String) extends Structure with Storable {

	/**
	 * A map of unit vector to block positions.
	 */
	var structure = Map.empty[Vector3i, String]

	override def getExteriorStructure: Set[Vector3i] = {
		return getBlockStructure.keySet
	}

	override def getBlockStructure: Map[Vector3i, Block] = {
		val matrix = new MatrixStack().translate(translate).scale(scale).rotate(rotation).getMatrix
		return structure
			.filter(kv => Game.instance.blockManager.getBlock(kv._2).isPresent)
			.map(e => (e._1.transform(matrix), Game.instance.blockManager.getBlock(e._2).get()))
	}

	override def load(data: Data) {
		structure = data.get("structure").asInstanceOf[util.Map[Vector3i, String]].toMap
	}

	override def save(data: Data) {
		data.put("structure", structure.asInstanceOf[util.Map[Vector3i, String]])
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
	override def volumeEquation(position: Vector3d): Double = Double.PositiveInfinity

	override def getID: String = name
}
