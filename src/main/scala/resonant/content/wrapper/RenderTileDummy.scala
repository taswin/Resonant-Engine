package resonant.content.wrapper

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import resonant.content.spatial.block.SpatialBlock
import resonant.lib.transform.vector.Vector3

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
class RenderTileDummy extends TileEntitySpecialRenderer
{
  def renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, f: Float)
  {
    if (tile.isInstanceOf[SpatialBlock])
    {
      val spatial = tile.asInstanceOf[SpatialBlock]
      spatial.renderDynamic(new Vector3(x, y, z), f, 0)
    }
  }
}