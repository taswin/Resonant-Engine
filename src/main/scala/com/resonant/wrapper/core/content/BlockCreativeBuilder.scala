package com.resonant.wrapper.core.content

import com.resonant.core.prefab.block.Rotatable
import com.resonant.wrapper.core.Reference
import com.resonant.wrapper.lib.schematic.Structure
import nova.core.block.Block
import nova.core.entity.Entity
import nova.core.game.Game
import nova.core.network.{NetworkManager, Packet, PacketReceiver, PacketSender}
import nova.core.util.transform.Vector3d

object BlockCreativeBuilder {
	var schematics: Seq[Structure] = Seq.empty
}

class BlockCreativeBuilder extends Block with Rotatable with PacketReceiver with PacketSender {

	//Constructor
	rotationMask = 0x3F

	/**
	 * Called when the block is right clicked by the player
	 */
	override def onRightClick(entity: Entity, side: Int, hit: Vector3d): Boolean = {
		Game.instance.get.guiFactory.get.showGui(Reference.id, "creativeBuilder", entity, position)
		return true
	}

	override def read(id: Int, packet: Packet) {
		super.read(id, packet)
		if (NetworkManager.instance.get().isServer && id == 1) {
			val schematicID = packet.readInt
			val size = packet.readInt
			val buildMap = BlockCreativeBuilder.schematics(schematicID).getStructure(direction, size)

			for (entry <- buildMap.entrySet()) {
				val placement = position + entry.getKey
				blockAccess.setBlock(placement, entry.getValue)
			}
		}
	}

	override def getID: String = "creativeBuilder"
}