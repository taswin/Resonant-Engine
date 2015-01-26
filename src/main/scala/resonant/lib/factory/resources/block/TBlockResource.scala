package resonant.lib.factory.resources.block

import cpw.mods.fml.relauncher.{Side, SideOnly}
import resonant.lib.factory.resources.ResourceFactory

/**
 * @author Calclavia
 */
trait TBlockResource extends ResonantBlock
{
  var resourceMaterial: String = ""

  @SideOnly(Side.CLIENT)
  override def colorMultiplier: Int = ResourceFactory.getColor(resourceMaterial)
}
