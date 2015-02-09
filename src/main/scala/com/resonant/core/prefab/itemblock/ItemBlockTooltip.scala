package com.resonant.core.prefab.itemblock

import com.resonant.core.prefab.item.TItemToolTip
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock

class ItemBlockTooltip(block: Block) extends ItemBlock(block) with TItemToolTip {
}