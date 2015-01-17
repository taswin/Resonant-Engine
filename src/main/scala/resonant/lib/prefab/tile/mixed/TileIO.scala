package resonant.lib.prefab.tile.mixed

import net.minecraft.block.material.Material
import resonant.lib.content.prefab.TIO
import resonant.lib.prefab.tile.spatial.SpatialTile

abstract class TileIO(material: Material) extends SpatialTile(material: Material) with TIO
{
}
