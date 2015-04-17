package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.{NodeElectric, NodeProvider}
import com.resonant.core.graph.internal.NodeBlockConnect
import com.resonant.wrapper.core.api.tile.DebugInfo

import scala.beans.BeanProperty
import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
abstract class NodeAbstractElectric(parent: NodeProvider) extends NodeBlockConnect[NodeElectric](parent) with DebugInfo with NodeElectric {

	@BeanProperty
	var resistance = 1d

	override def getDebugInfo = List(toString)
}
