package resonantengine.lib.render.wrapper

import nova.core.util.transform.Vector3d
import resonantengine.lib.modcontent.block.{BlockDummy, ResonantBlock}

object BlockRenderHandler extends ISimpleBlockRenderingHandler
{
  val ID = RenderingRegistry.getNextAvailableRenderId()

  override def renderInventoryBlock(block: Block, metadata: Int, modelID: Int, renderer: RenderBlocks)
  {
    if (block.isInstanceOf[BlockDummy])
    {
      val tile = (block.asInstanceOf[BlockDummy]).dummyTile

      glEnable(GL_RESCALE_NORMAL)
      glPushAttrib(GL_TEXTURE_BIT)
      glPushMatrix()
      tile.renderInventory(new ItemStack(block, 1, metadata))
      glPopMatrix()
      glPopAttrib()
    }
  }

  override def renderWorldBlock(access: IBlockAccess, x: Int, y: Int, z: Int, block: Block, modelId: Int, renderBlocks: RenderBlocks): Boolean =
  {
    var renderer: ResonantBlock = null

    /**
     * Try TileEntity rendering
     */
    val tile = access.getTileEntity(x, y, z)

    if (tile.isInstanceOf[ResonantBlock])
    {
      val spatial = tile.asInstanceOf[ResonantBlock]
      renderer = spatial.tile
    }

    /**
     * Try Block rendering
     */
    if (renderer == null && block.isInstanceOf[BlockDummy])
    {
      val dummy = block.asInstanceOf[BlockDummy]
      dummy.inject(access, x, y, z)
      renderer = dummy.getTile(access, x, y, z)
    }

    if (renderer != null)
    {
		return renderer.renderStatic(renderBlocks, new Vector3d(x, y, z), 0)
    }

    return false
  }

  override def shouldRender3DInInventory(modelId: Int) = true

  override def getRenderId = BlockRenderHandler.ID

}