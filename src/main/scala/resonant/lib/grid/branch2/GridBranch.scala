package resonant.lib.grid.branch2

import resonant.lib.grid.{GridNode, Grid}
import resonant.lib.grid.node.NodeGrid

/**
 * A grid that implements branching.
 *
 * @author Calclavia
 */
class GridBranch[N <: NodeGrid[N]](node: Class[N]) extends GridNode[N](node)
{
  /**
   * A set of branches that exist in this grid
   */
  var branches = Set.empty[Branch]

  override def reconstruct()
  {
    //Rebuilds the branches in the grid via pathfinding
    //TODO: Ensure the connections DO NOT change while pathing (when multithreaded)

  }

  def path(branch: Branch, current: N)
  {
    val con = current.connections
    var nextBranch : Branch = null

    //Check the type of connection
    con.size match
    {
      case x: Int if x > 2 =>
        //There are more than two connections; it must be a junction.
        if ()
        {

        }
        else
        {
          //Connection new junction to last part
          //Create new junction

        }
      case x: Int if x == 2 =>
        //There are only two connections; it must be a path
        //If the last part was a wire add this wire to it
        if (branch.isInstanceOf[Branch])
        {
          (branch.asInstanceOf[Branch]).add(currentNode)
          nextBranch = branch
        }
        else
        {
          nextBranch = new Branch
          (nextBranch.asInstanceOf[Branch]).add(currentNode)
          if (branch.isInstanceOf[Junction])
          {
            (nextBranch.asInstanceOf[Branch]).setConnectionA(branch)
            (branch.asInstanceOf[Junction]).addConnection(nextBranch)
          }
        }
      case _ =>

    }

  }
}
