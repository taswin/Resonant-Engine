package resonantengine.lib.prefab.tile.mixed

import net.minecraft.block.material.Material
import resonantengine.lib.content.prefab.TIO
import resonantengine.lib.prefab.tile.spatial.ResonantTile

abstract class TileIO(material: Material) extends ResonantTile(material: Material) with TIO
{
}
