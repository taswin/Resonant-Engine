package com.resonant.wrapper.core.api.tile

import java.util.Set

import net.minecraft.entity.player.EntityPlayer

/**
 * TPlayerUsing is a trait applied to all tiles that can have GUI. It is used to indicate which players the block needs to send a packet too. The set provided will be mutated by the ContainerBase class in the GUI.
 */
trait IPlayerUsing {
	def getPlayersUsing: Set[EntityPlayer]
}