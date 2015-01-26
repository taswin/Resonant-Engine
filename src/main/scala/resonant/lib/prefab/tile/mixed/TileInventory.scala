package resonant.lib.prefab.tile.mixed

import net.minecraft.block.material.Material
import resonant.lib.content.prefab.TInventory
import resonant.lib.prefab.tile.spatial.ResonantTile

/**
 * Used for Java class inheritance of Scala traits
 * @author Calclavia
 */
abstract class TileInventory(material: Material) extends ResonantTile(material: Material) with TInventory
{
}