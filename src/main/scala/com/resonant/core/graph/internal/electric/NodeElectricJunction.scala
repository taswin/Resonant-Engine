package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.NodeProvider
import com.resonant.core.graph.internal.electric.component.Junction

/**
 * Wires are getNodes in the grid that will not have different terminals, but instead can connect omni-directionally.
 * Wires will be treated as junctions and collapsed.
 * @author Calclavia
 */
class NodeElectricJunction(parent: NodeProvider) extends NodeElectric(parent) {

	var junction: Junction = null

	override def toString: String = {
		"ElectricJunction [" + connections.size() + ", " + BigDecimal(voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"
	}

}
