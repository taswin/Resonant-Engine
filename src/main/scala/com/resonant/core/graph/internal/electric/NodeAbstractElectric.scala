package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.{NodeElectric, NodeProvider}
import com.resonant.core.graph.internal.NodeBlockConnect
import com.resonant.wrapper.core.api.tile.DebugInfo

import scala.collection.convert.wrapAll._

/**
 * @author Calclavia
 */
abstract class NodeAbstractElectric(parent: NodeProvider) extends NodeBlockConnect[NodeElectric](parent) with DebugInfo with NodeElectric {

	private var _resistance = 1d

	protected[graph] var onResistanceChange = Seq.empty[(NodeElectric) => Unit]

	def resistance = _resistance

	def resistance_=(res: Double) {
		_resistance = res
		onResistanceChange.foreach(_.apply(this))
	}

	def getResistance = _resistance

	def setResistance(res: Double) = _resistance = res

	override def getDebugInfo = List(toString)
}
