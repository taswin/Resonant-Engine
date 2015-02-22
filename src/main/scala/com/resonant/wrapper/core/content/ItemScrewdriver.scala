package com.resonant.wrapper.core.content

import java.util.Optional

import buildcraft.api.tools.IToolWrench
import com.resonant.wrapper.core.ResonantEngine
import net.minecraft.entity.player.EntityPlayer
import nova.core.item.Item
import nova.core.render.texture.ItemTexture
import nova.core.util.Category

class ItemScrewdriver extends Item with IToolWrench with Category {

	override def getID: String = "screwdriver"

	override def canWrench(entityPlayer: EntityPlayer, x: Int, y: Int, z: Int): Boolean = true

	override def wrenchUsed(entityPlayer: EntityPlayer, x: Int, y: Int, z: Int) {
	}

	override def getTexture: Optional[ItemTexture] = Optional.of(ResonantEngine.textureScrewdriver)

	override def getCategory: String = "tools"
}