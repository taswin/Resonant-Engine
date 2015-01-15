package resonant.lib.grid.electric

import resonant.api.IUpdate
import resonant.lib.grid.{GridNode, UpdateTicker}

import scala.collection.convert.wrapAll._

/**
 * A direct current electricity grid.
 *
 * Thanks to Naiten for
 *
 * @author Calclavia
 */
class GridDC extends GridNode[NodeDC](classOf[NodeDC]) with IUpdate
{
  /**
   * Intersections can be thought of as the edges of each node
   * There should always be (node.size - 1) amount of intersections.
   */
  var intersections = Set.empty[Intersection]

  /**
   * Links can be thought of as the path between one node to the other
   * There should always be node.size amount of links
   */
  var links = Set.empty[Link]

  /**
   * Reconstruct must build the links and intersections of the grid
   */
  override def reconstruct(first: NodeDC)
  {
    intersections = Set.empty[Intersection]
    links = Set.empty[Link]
    super.reconstruct(first)
    UpdateTicker.world.addUpdater(this)
  }

  override def update(deltaTime: Double)
  {
    links.foreach(_.calculate())

    /**
     * Potential difference creates current, which acts to decrease potential difference.
     * Any system forwards to minimal inner energy, and only equipotential systems have minimal energy.
     */
    intersections.foreach(
      intersection =>
      {
        intersection.links.foreach(
          link =>
          {
            link.calculate()
            val delta = link.current * deltaTime

            if (intersection == link.intersectionA)
              intersection.potential += delta
            else if (intersection == link.intersectionB)
              intersection.potential -= delta
          }
        )
      }
    )
  }

  override def continueUpdate: Boolean = canUpdate

  override def canUpdate: Boolean = getNodes.size > 0

  override protected def populateNode(node: NodeDC, prev: NodeDC)
  {
    super.populateNode(node, prev)

    if (prev != null)
    {
      //Build an intersection between prev and node
      val intersection = new Intersection
      intersection.links :+=
      node.connections.foreach(n => populate(n, node))
      links :+= new Link(prev, node)
    }
  }
}
