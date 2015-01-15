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
   * There should always at least (node.size - 1) amount of intersections.
   */
  var intersections = Set.empty[Intersection]

  /**
   * Links can be thought of as the path between one node to the other
   * There should always be node.size amount of links
   */
  var links = Set.empty[Link]

  var componentMap = Map.empty[NodeDC, Link]
  var recursed = Set.empty[NodeDC]
  private var intersectionBuilder = Seq.empty[Intersection]
  private var recusedNodes = Seq.empty[NodeDC]

  /**
   * Reconstruct must build the links and intersections of the grid
   */
  override def reconstruct(first: NodeDC)
  {
    intersections = Set.empty[Intersection]
    links = Set.empty[Link]

    super.reconstruct(first)


    //Build links based on the recusion
    val headLastLink = new Link(intersectionBuilder.last, intersectionBuilder.head)
    links += headLastLink
    componentMap += recusedNodes(0) -> headLastLink

    for (i <- 0 until intersectionBuilder.size - 1)
    {
      val link = new Link(intersectionBuilder(i), intersectionBuilder(i + 1))
      links += link
      componentMap += recusedNodes(i + 1) -> link
    }

    links.foreach(_.reconstruct())

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

  protected def recurseNode(node: NodeDC, prev: NodeDC, prevIntersection: Intersection = null)
  {
    recursed += node

    //If the node has more than two connections, we can build a link into two intersections.
    if (node.connections.size >= 2)
    {
      //Find or create new intersections
      val interA = if (prevIntersection == null) new Intersection else prevIntersection
      val interB = new Intersection

      //Build link between the intersections
      val link = new Link(interA, interB)
      componentMap += node -> link

      node.connections.filterNot(recursed.contains).foreach(next => recurseNode(next, node, interB))
    }
  }
}
