package resonant.core.content

import net.minecraft.block.Block
import resonant.core.Reference
import resonant.core.content.debug.TileCreativeBuilder
import resonant.lib.mod.content.ContentHolder

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
