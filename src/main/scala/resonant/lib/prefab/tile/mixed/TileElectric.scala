package resonant.lib.prefab.tile.mixed

import net.minecraft.block.material.Material
import resonant.lib.prefab.tile.spatial.SpatialTile
import resonant.lib.prefab.tile.traits.TElectric

class TileElectric(material: Material) extends SpatialTile(material: Material) with TElectric
{
}
