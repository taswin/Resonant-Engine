package resonant.lib.grid.branch2

import resonant.lib.grid.GridNode
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
  var mainBranch: Branch = null

  override def reconstruct()
  {
    //Rebuilds the branches in the grid via pathfinding
    //TODO: Ensure the connections DO NOT change while pathfinding (when multithreaded)

    mainBranch = new SeriesBranch()
    solve()
  }

  /**
   * Solves the branches of the grid
   */
  def solve()
  {
    
  }
}
