package com.resonant.wrapper.core.content

import java.util.Optional

import com.resonant.core.prefab.block.Rotatable
import com.resonant.wrapper.core.{Reference, ResonantEngine}
import com.resonant.wrapper.lib.schematic.Structure
import nova.core.block.Block
import nova.core.entity.Entity
import nova.core.game.Game
import nova.core.network.{Packet, PacketReceiver, PacketSender}
import nova.core.render.texture.Texture
import nova.core.util.transform.Vector3d
import nova.core.util.{Category, Direction}

object BlockCreativeBuilder {
	var schematics: Seq[Structure] = Seq.empty
}

class BlockCreativeBuilder extends Block with Rotatable with PacketReceiver with PacketSender with Category {

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
		if (Game.instance.get().networkManager.isServer && id == 1) {
			val schematicID = packet.readInt
			val size = packet.readInt
			val buildMap = BlockCreativeBuilder.schematics(schematicID).getBlockStructure
			buildMap.foreach(kv => {
				val placement = position + kv._1
				blockAccess.setBlock(placement, kv._2)
			})
		}
	}

	override def getTexture(side: Direction): Optional[Texture] = Optional.of(ResonantEngine.textureCreativeBuilder)

	override def getID: String = "creativeBuilder"

	override def getCategory: String = "tools"
}