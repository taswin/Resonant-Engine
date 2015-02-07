package com.resonant.lib.modcontent.block

import java.util.{HashSet => JHashSet, Set => JSet}

import com.resonant.core.api.tile.IPlayerUsing
import com.resonant.prefab.block.impl.TTicker
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.Packet

/**
 * All tiles inherit this class.
 *
 * @author Calclavia
 */
class ResonantTile(material: Material) extends ResonantBlock(material) with IPlayerUsing with TTicker {
	/**
	 * The players to send packets to for machine update info.
	 */
	final val playersUsing = new JHashSet[EntityPlayer]()

	override def update() {
		super[TTicker].update()
	}

	override def getDescriptionPacket: Packet = {
		ResonantEngine.packetHandler.toMCPacket(getDescPacket)
	}

	def getDescPacket: AbstractPacket = null

	override def tile: ResonantTile = this

	override def getPlayersUsing: JSet[EntityPlayer] = playersUsing
}