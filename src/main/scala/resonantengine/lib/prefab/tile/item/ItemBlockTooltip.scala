package resonantengine.lib.prefab.tile.item

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import resonantengine.lib.prefab.item.TItemToolTip

class ItemBlockTooltip(block: Block) extends ItemBlock(block) with TItemToolTip
{
}