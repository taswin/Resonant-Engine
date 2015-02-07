package com.resonant.prefab.block.itemblock

import com.resonant.prefab.item.TItemToolTip
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock

class ItemBlockTooltip(block: Block) extends ItemBlock(block) with TItemToolTip {
}