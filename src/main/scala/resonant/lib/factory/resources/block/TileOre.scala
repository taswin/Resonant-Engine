package resonant.lib.factory.resources.block

import net.minecraft.block.material.Material
import resonant.engine.Reference
import resonant.lib.prefab.tile.spatial.SpatialBlock

/**
 * @author Calclavia
 */
class TileOre extends SpatialBlock(Material.rock) with TBlockResource
{
  setTextureName(Reference.PREFIX + "ore")
}
