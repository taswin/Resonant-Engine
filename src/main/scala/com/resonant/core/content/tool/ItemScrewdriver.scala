package com.resonant.core.content.tool

import java.util.Optional

import buildcraft.api.tools.IToolWrench
import com.resonant.core.content.ResonantContent
import net.minecraft.entity.player.EntityPlayer
import nova.core.item.Item
import nova.core.render.texture.ItemTexture

class ItemScrewdriver extends Item with IToolWrench {

	override def getID: String = "screwdriver"

	override def canWrench(entityPlayer: EntityPlayer, x: Int, y: Int, z: Int): Boolean = true

	override def wrenchUsed(entityPlayer: EntityPlayer, x: Int, y: Int, z: Int) {
	}

	override def getTexture: Optional[ItemTexture] = {
		return Optional.of(ResonantContent.screwTexture)
	}
}