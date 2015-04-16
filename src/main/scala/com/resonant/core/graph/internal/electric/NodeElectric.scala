package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.NodeProvider
import com.resonant.core.graph.internal.NodeBlockConnect
import com.resonant.wrapper.core.api.tile.DebugInfo

import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
abstract class NodeElectric(parent: NodeProvider) extends NodeBlockConnect[NodeElectric](parent) with DebugInfo with com.resonant.core.graph.api.NodeElectric {

	override def getDebugInfo = List(toString)
}
