package com.resonant.core.access

import nova.core.player.Player
import nova.core.retention.Stored

class AccessUser(@Stored var username: String) extends AbstractAccess {

	def this(player: Player) {
		this(player.getDisplayName)
	}

	override def hasPermission(username: String, permission: Permission): Boolean = hasPermission(permission)

	def hasPermission(permission: Permission): Boolean = permissions.contains(permission)
}