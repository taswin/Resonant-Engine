package resonant.lib.grid.energy.electric

import resonant.api.IUpdate
import resonant.lib.grid.core.{GridNode, UpdateTicker}

import scala.collection.convert.wrapAll._

/**
 * A direct current electricity grid.
 *
 * Thanks to Naiten for
 *
 * @author Calclavia
 */
class GridDC extends GridNode[NodeDC] with IUpdate
{
  /**
   * There should always at least (node.size - 1) amount of intersections.
   */
  var junctions = Set.empty[Junction]

  nodeClass = classOf[NodeDC]

  /**
   * Reconstruct must build the links and intersections of the grid
   */
  override def reconstruct(first: NodeDC)
  {
    junctions = Set.empty[Junction]
    super.reconstruct(first)
    solveWires()
    solveGraph()
    UpdateTicker.world.addUpdater(this)
  }

  /**
   * Collapse all wires into junctions. These junctions will be referenced to
   */
  private def solveWires()
  {
    /**
     * Finds all the wire nodes connected to this one.
     */
    def recurseFind(wire: NodeDCJunction, result: Set[NodeDCJunction] = Set.empty[NodeDCJunction]): Set[NodeDCJunction] =
    {
      val wireConnections = wire.connections.filter(_.isInstanceOf[NodeDCJunction]).map(_.asInstanceOf[NodeDCJunction])
      var newResult = result + wire
      newResult ++= wireConnections.filterNot(result.contains).map(n => recurseFind(n, newResult)).flatten
      return newResult
    }

    var recursed = Set.empty[NodeDC]

    val nodes = getNodes.filter(_.isInstanceOf[NodeDCJunction]).map(_.asInstanceOf[NodeDCJunction])

    for (node <- nodes)
    {
      if (!recursed.contains(node))
      {
        //Create a junction
        val junction = new Junction
        val foundWires = recurseFind(node).toSet[NodeDC]
        recursed ++= foundWires
        junction.wires = foundWires
        junction.nodes = foundWires.map(_.connections).flatten.filterNot(_.isInstanceOf[NodeDCJunction])
        foundWires.foreach(
          w =>
          {
            w.junctionA = junction
            w.junctionB = junction
          }
        )
        junctions += junction
      }
    }
  }

  /**
   * Populates the node and junctions recursively
   * TODO: Unit test the grid population algorithm
   */
  private def solveGraph()
  {
    var recursed = Set.empty[NodeDC]

    def solveGraph(node: NodeDC, prev: NodeDC = null)
    {
      //Check if we already traversed through this node and if it is valid. Proceed if we haven't already done so.
      if (!recursed.contains(node))
      {
        //Add this node into the list of nodes.
        recursed += node

        //If the node has at least a positive and negative connection, we can build two junctions across it.
        if (node.positives.size > 0 && node.negatives.size > 0)
        {
          //Use the junction that this node came from. If this is the first node being recursed, then create a new junction.
          node.junctionA =
            {
              if (prev == null)
              {
                /**
                 * Look through all junctions, see if there is already one that is connected to this junction, but NOT the previous junction
                 * If the junction does NOT exist, then create a new one
                 */
                junctions.find(j => j.nodes.contains(node) && j.wires.exists(node.negatives.contains)) match
                {
                  case Some(x) => x
                  case _ =>
                    println("Warning: Creating new junction. This should not happen yet.")
                    new Junction //Rarely should we need to create a new junction
                }
              }
              else
              {
                prev.junctionB
              }
            }

          //Create a new junction for intersection B
          node.junctionB =
            {
              /**
               * Look through all junctions, see if there is already one that is connected to this junction, but NOT the previous junction
               * If the junction does NOT exist, then create a new one
               */
              junctions.find(j => j.nodes.contains(node) /* && j.wires.exists(node.positives.contains)*/ && node.junctionA != j) match
              {
                case Some(x) => x
                case _ =>
                  println("Warning: Creating new junction. This should not happen yet.")
                  new Junction //Rarely should we need to create a new junction
              }
            }

          junctions += node.junctionA
          junctions += node.junctionB

          //Recursively populate for all nodes connected to junction B, because junction A simply goes backwards in the graph. There is no point iterating it.
          node.junctionB.nodes.foreach(next => solveGraph(next, node))
        }
        else
        {
          println("Found invalid DC Node")
        }
      }
    }

    getNodes.filterNot(_.isInstanceOf[NodeDCJunction]).headOption match
    {
      case Some(x) => solveGraph(x)
      case _ =>
    }
  }

  override def deconstruct(first: NodeDC)
  {
    super.deconstruct(first)
    UpdateTicker.world.removeUpdater(this)
  }

  override def update(deltaTime: Double)
  {
    //Calculate all nodes except batteries
    junctions.foreach(_.update(deltaTime * 10))
    nodes.foreach(_.nextVoltage = 0)
  }

  override def updatePeriod: Int = if (getNodes.size > 0) 20 else 0

  override protected def populateNode(node: NodeDC, prev: NodeDC)
  {
    super.populateNode(node, prev)
    node.junctionA = null
    node.junctionB = null
    node.voltage = 0
    node.current = 0
  }
}