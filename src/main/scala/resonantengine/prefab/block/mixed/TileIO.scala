package resonantengine.prefab.block.mixed

import net.minecraft.block.material.Material
import resonantengine.lib.content.prefab.TIO
import resonantengine.lib.modcontent.block.ResonantTile

abstract class TileIO(material: Material) extends ResonantTile(material: Material) with TIO
{
}
