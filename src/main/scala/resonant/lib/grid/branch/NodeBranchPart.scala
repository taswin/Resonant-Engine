package resonant.lib.grid.branch

import net.minecraftforge.common.util.ForgeDirection
import resonant.api.grid.{IGrid, INodeGrid, INodeProvider}
import resonant.lib.grid.branch.part.Part
import resonant.lib.grid.node.Node
import resonant.lib.prefab.TConnector
import resonant.lib.transform.vector.VectorWorld

import scala.beans.BeanProperty
;

/**
 * A node that is part of a branch
 * @author DarkCow
 */
class NodeBranchPart(parent: INodeProvider) extends Node(parent) with TConnector[NodeBranchPart] with INodeGrid
{
  @BeanProperty
  var branch : Part = null

  var grid : BranchedGrid = null

  /** Sets the grid reference */
  override def setGrid(grid: IGrid[_]):Unit =
  {
    if(grid.isInstanceOf[BranchedGrid])
      this.grid = grid.asInstanceOf[BranchedGrid]
    else if(grid == null)
      this.grid = null
  }

  /** Gets the grid reference */
  override def getGrid: BranchedGrid =
  {
    if(grid == null)
    {
      //If no grid found threw connections
      if(grid == null)
      {
        grid = new BranchedGrid()
        grid.add(this)
      }
    }
    return grid
  }

  /** Is this connector allowed to connect to any side
    * @param connector - any connecting object, Most likely TileEntity, Node, INodeProvider */
  override protected def isValidConnector(connector: NodeBranchPart): Boolean =
  {
    return connector != null;
  }

  override def reconstruct()
  {
    super.reconstruct()
    buildConnections()
  }

  override def deconstruct()
  {
    super.deconstruct()
    getConnections.clear()
    setGrid(null)
    if(branch != null)
      branch.remove(this)
    branch = null
  }


  override def connect(obj: NodeBranchPart, dir: ForgeDirection)
  {
    super.connect(obj, dir)
    if(obj.getGrid != this.getGrid)
      obj.getGrid.merge(getGrid)
  }

  override def updateConnection(dir: ForgeDirection, loc: VectorWorld)
  {
    val tile = loc.getTileEntity
    if(tile != null && tile.isInstanceOf[INodeProvider])
    {
        val node : NodeBranchPart = tile.asInstanceOf[INodeProvider].getNode(classOf[NodeBranchPart], dir.getOpposite)

        if (canConnect(node, dir.getOpposite))
        {
          connect(node, dir)
        }
    }
  }
}