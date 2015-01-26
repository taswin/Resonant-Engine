package resonantengine.core.content

import net.minecraft.block.Block
import resonantengine.core.Reference
import resonantengine.core.content.debug.TileCreativeBuilder
import resonantengine.core.content.tool.ItemScrewdriver
import resonantengine.prefab.modcontent.ContentHolder

/**
 * Resonant Engine content loader
 * @author Calclavia
 */
object ResonantContent extends ContentHolder
{
  var blockCreativeBuilder: Block = new TileCreativeBuilder
  var itemWrench = new ItemScrewdriver

  manager.setPrefix(Reference.prefix)
}
