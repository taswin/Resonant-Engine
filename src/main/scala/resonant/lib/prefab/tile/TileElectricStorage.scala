package resonant.lib.prefab.tile

import net.minecraft.block.material.Material
import resonant.lib.content.prefab.TEnergyStorage
import resonant.lib.prefab.tile.spatial.SpatialTile

/**
 * @since 27/05/14
 * @author tgame14
 * @deprecated no usage
 */
@Deprecated
class TileElectricStorage(material: Material) extends SpatialTile(material) with TEnergyStorage
{
}
