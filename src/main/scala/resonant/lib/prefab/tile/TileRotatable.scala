package resonant.lib.prefab.tile

import net.minecraft.block.material.Material
import resonant.lib.prefab.tile.spatial.SpatialTile
import resonant.lib.prefab.tile.traits.TRotatable

/**
 * @since 27/05/14
 * @author tgame14
 */
class TileRotatable(material: Material) extends SpatialTile(material: Material) with TRotatable
{
}
