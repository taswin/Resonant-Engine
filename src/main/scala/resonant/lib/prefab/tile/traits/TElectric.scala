package resonant.lib.content.prefab

import net.minecraft.block.Block
import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.INodeProvider
import resonant.api.tile.node.INode
import resonant.lib.grid.electric.NodeDC
import resonant.lib.prefab.tile.spatial.SpatialTile

/**
 * A trait for all INodeProviders that implement a DC circuit. Nodes must handle energy storage themself.
 */
trait TElectric extends SpatialTile with INodeProvider
{
  protected var dcNode = new NodeDC(this)

  override def start()
  {
    super.start()
    dcNode.reconstruct()
  }

  override def onWorldJoin()
  {
    dcNode.reconstruct()
  }

  override def onNeighborChanged(block: Block)
  {
    dcNode.reconstruct()
  }

  override def onWorldSeparate()
  {
    dcNode.deconstruct()
  }

  @deprecated
  def getVoltage = 120

  override def getNode[N <: INode](nodeType: Class[_ <: N], from: ForgeDirection): N =
  {
    if (classOf[NodeDC].isAssignableFrom(nodeType))
      return dcNode.asInstanceOf[N]

    return null.asInstanceOf[N]
  }
}