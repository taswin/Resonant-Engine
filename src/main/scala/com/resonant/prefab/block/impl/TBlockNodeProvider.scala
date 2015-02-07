package com.resonant.prefab.block.impl

import com.resonant.core.api.misc.ISave
import nova.core.util.transform.Vector3d

import scala.collection.convert.wrapAll._

/**
 * A node trait that can be mixed into any Tile. Mixing this trait will cause nodes to reconstruct/deconstruct as needed.
 * @author Calclavia
 */
trait TBlockNodeProvider extends ResonantTile with TNodeProvider {
	override def start() {
		super.start()

		if (!world.isRemote) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def onWorldJoin() {
		if (!world.isRemote) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def onNeighborChanged(block: Block) {
		if (!world.isRemote) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def onNeighborChanged(pos: Vector3d) {
		if (!world.isRemote) {
			nodes.foreach(_.reconstruct())
		}
	}

	override def onWorldSeparate() {
		if (!world.isRemote) {
			nodes.foreach(_.deconstruct())
		}
	}

	override def invalidate() {
		if (!world.isRemote) {
			nodes.foreach(_.deconstruct())
		}
	}

	override def writeToNBT(nbt: NBTTagCompound) {
		super.writeToNBT(nbt)
		nodes.filter(_.isInstanceOf[ISave]).foreach(_.asInstanceOf[ISave].save(nbt))
	}

	override def readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		nodes.filter(_.isInstanceOf[ISave]).foreach(_.asInstanceOf[ISave].load(nbt))
	}
}
