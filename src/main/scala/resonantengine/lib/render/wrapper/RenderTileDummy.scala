package resonantengine.lib.render.wrapper

import nova.core.util.transform.Vector3d
import resonantengine.lib.modcontent.block.ResonantBlock

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
class RenderTileDummy extends TileEntitySpecialRenderer
{
  def renderTileEntityAt(tile: TileEntity, x: Double, y: Double, z: Double, f: Float)
  {
    if (tile.isInstanceOf[ResonantBlock])
    {
      val spatial = tile.asInstanceOf[ResonantBlock]
      spatial.renderDynamic(new Vector3d(x, y, z), f, 0)
    }
  }
}