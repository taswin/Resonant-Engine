package resonant.lib.prefab.tile.mixed

import net.minecraft.block.material.Material
import resonant.lib.content.prefab.TIO
import resonant.lib.prefab.tile.spatial.ResonantTile

abstract class TileIO(material: Material) extends ResonantTile(material: Material) with TIO
{
}
