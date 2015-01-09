package resonant.lib.factory.resources.block

import cpw.mods.fml.relauncher.{Side, SideOnly}
import resonant.lib.factory.resources.ResourceFactory
import resonant.lib.prefab.tile.spatial.SpatialBlock

/**
 * @author Calclavia
 */
trait TBlockResource extends SpatialBlock
{
  var resourceMaterial: String = ""

  @SideOnly(Side.CLIENT)
  override def colorMultiplier: Int = ResourceFactory.getColor(resourceMaterial)
}
