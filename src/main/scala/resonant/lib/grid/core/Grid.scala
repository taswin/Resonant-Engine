package resonant.lib.grid.core

import java.util.{Set => JSet}

import resonant.api.IGrid

import scala.beans.BeanProperty
import scala.collection.convert.wrapAll._
import scala.reflect.internal.util.WeakHashSet

/**
 * Collection of nodes patterened in a grid
 */
class Grid[N <: AnyRef] extends IGrid[N]
{
  val nodes = new WeakHashSet[N]
  //    Collections.newSetFromMap(mutable.WeakHashMap.empty[N,Boolean])
  //    Collections.newSetFromMap(new util.WeakHashMap[N, Boolean])

  @BeanProperty
  var nodeClass: Class[N] = null

  /**
   * Destroys the grid and all of its data
   */
  def deconstruct()
  {
    nodes synchronized
    {
      nodes.clear()
    }
  }

  /**
   * Called to rebuild the grid node by node
   */
  def reconstruct()
  {

  }

  /**
   * Checks to see if the node is valid
   */
  def isValidNode(node: AnyRef): Boolean = nodeClass == null || node != null && nodeClass.isAssignableFrom(node.getClass)

  /**
   * Adds an object to the node list
   */
  def add(node: N)
  {
    nodes.add(node)
  }

  /**
   * Removes a node from the node list.
   */
  def remove(node: N)
  {
    nodes.remove(node)
  }

  /**
   * Gets the list of all nodes
   */
  def getNodes: JSet[N] = nodes

  override def toString = getClass.getSimpleName + "[" + hashCode + ", Nodes: " + nodes.size + "]"
}