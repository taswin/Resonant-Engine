package com.resonant.core.prefab.block

import _root_.java.util

import com.resonant.wrapper.core.api.tile.IIO
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText
import nova.core.block.Block
import nova.core.util.Direction
import nova.core.util.components.Storable

import scala.collection.convert.wrapAll._

/**
 * A Trait that handles input and outputs
 *
 * @author Calclavia
 */
//TODO: Using ternary is kind of ineffective
trait IO extends Block with Storable with IIO {

	/**
	 * IO METHODS.
	 * Default: Connect from all sides. "111111"
	 * Output all sides: 728
	 * 0 - Nothing
	 * 1 - Input
	 * 2 - Output
	 */
	protected var ioMap = 364
	protected var saveIOMap = false

	def toggleIO(side: Int, entityPlayer: EntityPlayer): Boolean = {
		if (!world.isRemote) {
			val newIO = (getIO(Direction.getOrientation(side)) + 1) % 3
			setIO(Direction.getOrientation(side), newIO)

			if (!world.isRemote) {
				entityPlayer.addChatMessage(new ChatComponentText("Side changed to: " + (if (newIO == 0) "None" else (if (newIO == 1) "Input" else "Output"))))
			}

			world.notifyBlocksOfNeighborChange(x, y, z, block)
		}
		return true
	}

	override def setIO(dir: Direction, ioType: Int) {
		val currentIO: String = getIOMapBase3
		val str: StringBuilder = new StringBuilder(currentIO)
		str.setCharAt(dir.ordinal, Integer.toString(ioType).charAt(0))
		ioMap = Integer.parseInt(str.toString, 3)
	}

	/**
	 * The electrical input direction.
	 *
	 * @return The direction that electricity is entered into the tile. Return null for no input. By
	 *         default you can accept power from all sides.
	 */
	override def getInputDirections: util.HashSet[Direction] = {
		var dirs = new util.HashSet[Direction]()

		for (direction <- Direction.DIRECTIONS) {
			if (getIO(direction) == 1) {
				dirs += direction
			}
		}
		return dirs
	}

	/**
	 * The electrical output direction.
	 *
	 * @return The direction that electricity is output from the tile. Return null for no output. By
	 *         default it will return an empty EnumSet.
	 */
	override def getOutputDirections: util.HashSet[Direction] = {
		var dirs = new util.HashSet[Direction]()

		for (direction <- Direction.DIRECTIONS) {
			if (getIO(direction) == 2) {
				dirs += direction
			}
		}

		return dirs
	}

	override def getIO(dir: Direction): Int = {
		val currentIO: String = getIOMapBase3
		return Integer.parseInt("" + currentIO.charAt(dir.ordinal))
	}

	def getIOMapBase3: String = {
		var currentIO: String = Integer.toString(ioMap, 3)
		while (currentIO.length < 6) {
			currentIO = "0" + currentIO
		}
		return currentIO

	}

	override def save(data: util.Map[String, AnyRef]) {
		super.save(data)

		if (saveIOMap) {
			data.put("ioMap", ioMap.toShort)
		}
	}

	override def load(data: util.Map[String, AnyRef]) {
		super.load(data)

		if (saveIOMap) {
			ioMap = data.getOrDefault("ioMap", 364).asInstanceOf[Int]
		}
	}

}