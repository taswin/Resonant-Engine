package resonant.lib.prefab.tile.mixed

import net.minecraft.block.material.Material
import resonant.lib.content.prefab.{TElectric, TInventory}
import resonant.lib.prefab.tile.spatial.SpatialTile

/**
 * Used for Java class inheritance of Scala traits
 * @author Calclavia
 */
abstract class TileElectricInventory(material: Material) extends SpatialTile(material: Material) with TInventory with TElectric
{
}
