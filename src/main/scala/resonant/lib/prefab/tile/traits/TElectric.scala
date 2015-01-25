package resonant.lib.prefab.tile.traits

import net.minecraft.block.Block
import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.INodeProvider
import resonant.api.tile.node.INode
import resonant.lib.grid.energy.electric.NodeElectricComponent
import resonant.lib.prefab.tile.spatial.SpatialTile

/**
 * A trait for all INodeProviders that implement a electric circuit. Nodes must handle energy storage themselves.
 */
@deprecated
trait TElectric extends SpatialTile with INodeProvider
{
  protected var electricNode = new NodeElectricComponent(this)

  override def start()
  {
    super.start()
    electricNode.reconstruct()
  }

  override def onWorldJoin()
  {
    electricNode.reconstruct()
  }

  override def onNeighborChanged(block: Block)
  {
    electricNode.reconstruct()
  }

  override def onWorldSeparate()
  {
    electricNode.deconstruct()
  }

  override def getNode[N <: INode](nodeType: Class[_ <: N], from: ForgeDirection): N =
  {
    if (classOf[NodeElectricComponent].isAssignableFrom(nodeType))
      return electricNode.asInstanceOf[N]

    return null.asInstanceOf[N]
  }
}
