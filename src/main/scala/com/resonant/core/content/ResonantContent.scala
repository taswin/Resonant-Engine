package com.resonant.core.content

import com.resonant.core.Reference
import com.resonant.core.content.debug.TileCreativeBuilder
import com.resonant.core.content.tool.ItemScrewdriver
import com.resonant.prefab.modcontent.ContentHolder
import net.minecraft.block.Block

/**
 * Resonant Engine content loader
 * @author Calclavia
 */
object ResonantContent extends ContentHolder {
	var blockCreativeBuilder: Block = new TileCreativeBuilder
	var itemWrench = new ItemScrewdriver

	manager.setPrefix(Reference.prefix)
}
