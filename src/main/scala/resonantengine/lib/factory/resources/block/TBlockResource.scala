package resonantengine.lib.factory.resources.block

import cpw.mods.fml.relauncher.{Side, SideOnly}
import resonantengine.lib.factory.resources.ResourceFactory
import resonantengine.lib.prefab.tile.spatial.ResonantBlock

/**
 * @author Calclavia
 */
trait TBlockResource extends ResonantBlock
{
  var resourceMaterial: String = ""

  @SideOnly(Side.CLIENT)
  override def colorMultiplier: Int = ResourceFactory.getColor(resourceMaterial)
}
