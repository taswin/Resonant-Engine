package resonantengine.prefab.block.impl

import java.util
import java.util.{List => JList}

import nova.core.util.Direction
import resonantengine.api.graph.INodeProvider
import resonantengine.api.graph.node.INode
import resonantengine.api.tile.IDebugInfo
import resonantengine.lib.grid.core.Node

import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
trait TNodeProvider extends INodeProvider with IDebugInfo
{
  protected val nodes = new util.HashSet[Node]

	override def getNode[N <: INode](nodeType: Class[_ <: N], from: Direction): N =
  {
    return nodes.filter(node => nodeType.isAssignableFrom(node.getClass)).headOption.getOrElse(null).asInstanceOf[N]
  }

  override def getDebugInfo: JList[String] =
  {
    val debugs = nodes.toList.filter(_.isInstanceOf[IDebugInfo])

    if (debugs.size > 0)
    {
      return debugs.map(_.asInstanceOf[IDebugInfo].getDebugInfo.toList).reduceLeft(_ ::: _)
    }

    return List[String]()
  }
}
