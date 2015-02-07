package com.resonant.prefab.network

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.tileentity.TileEntity

/**
 * Implement this if an object can send a packet with an id
 *
 * GetDescriptionPacket will FORWARD with an packet id of zero to the write method
 *
 * @author Calclavia
 */
trait TPacketSender extends TileEntity {
	override def getDescriptionPacket = ResonantEngine.packetHandler.toMCPacket(getPacket(0))

	def getPacket(id: Int): PacketTile = {
		val packet = new PacketTile(this)
		write(packet.data, id)
		return packet
	}

	/**
	 * Override this method
	 * Be sure to super this method or manually write the id into the packet when sending
	 */
	def write(buf: ByteBuf, id: Int) {
		buf <<< id
	}

	/** Sends the desc packet to all players around this tile */
	def sendDescPacket() {
		sendPacket(0)
	}

	/**
	 * Sends the packet to all players
	 * @param packet - packet to send
	 */
	def sendPacket(packet: AbstractPacket) {
		if (!getWorldObj.isRemote) {
			ResonantEngine.packetHandler.sendToAll(packet)
		}
		else {
			throw new RuntimeException("[TPacketReceiver] Trying to send a packet to clients from client side.")
		}
	}

	def sendPacket(id: Int, distance: Double = 64) {
		if (distance > 0) {
			sendPacket(getPacket(id), distance)
		}
		else {
			sendPacket(getPacket(id))
		}
	}

	/** Sends the packet to all players around this tile
	  * @param packet - packet to send
	  * @param distance - distance in blocks to search for players
	  */
	def sendPacket(packet: AbstractPacket, distance: Double) {
		if (!getWorldObj.isRemote) {
			ResonantEngine.packetHandler.sendToAllAround(packet, this, distance)
		}
		else {
			throw new RuntimeException("[TPacketReceiver] Trying to send a packet to clients from client side.")
		}
	}

	/** Sends the packet to the player. Useful for updating GUI information of those with GUIs open.
	  * @param player - player to send the packet to
	  * @param packet - packet to send
	  */
	def sendPacket(packet: AbstractPacket, player: EntityPlayer) {
		if (!getWorldObj.isRemote) {
			ResonantEngine.packetHandler.sendToPlayer(packet, player.asInstanceOf[EntityPlayerMP])
		}
		else {
			throw new RuntimeException("[TPacketReceiver] Trying to send a packet to clients from client side.")
		}
	}
}
