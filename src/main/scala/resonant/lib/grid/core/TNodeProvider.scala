package resonant.lib.grid.core

import java.util
import java.util.{List => JList}

import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.INodeProvider
import resonant.api.tile.node.INode
import resonant.lib.debug.IDebugInfo

import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
trait TNodeProvider extends INodeProvider with IDebugInfo
{
  protected val nodes = new util.HashSet[Node]

  override def getNode[N <: INode](nodeType: Class[_ <: N], from: ForgeDirection): N =
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
