package resonant.lib.prefab.tile

import net.minecraft.block.material.Material
import resonant.lib.content.prefab.TInventory
import resonant.lib.prefab.tile.spatial.SpatialTile

@deprecated
abstract class TileInventory(material: Material) extends SpatialTile(material: Material) with TInventory
{
  var maxSlots = 0

  override def getSizeInventory = maxSlots

  def setSizeInventory(slots: Int)
  {
    maxSlots = slots
  }
}