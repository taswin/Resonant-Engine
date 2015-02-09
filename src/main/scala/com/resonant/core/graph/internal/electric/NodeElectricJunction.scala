package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.NodeProvider

/**
 * Wires are nodes in the grid that will not have different terminals, but instead can connect omni-directionally.
 * Wires will be treated as junctions and collapsed.
 * @author Calclavia
 */
class NodeElectricJunction(parent: NodeProvider) extends NodeElectricComponent(parent) {

	override def toString: String = {
		if (junctionA != null) {
			"DC [" + connections.size() + " Total: " + BigDecimal(junctionA.voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) +
				"V Source: " + BigDecimal(junctionA.sourceVoltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V " +
				"In Current: " + BigDecimal(junctionA.currentIn).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A " +
				"Out Current: " + BigDecimal(junctionA.currentOut).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A]"
		}
		else {
			"DC [" + connections.size() + " Null Junction]"
		}
	}

}
