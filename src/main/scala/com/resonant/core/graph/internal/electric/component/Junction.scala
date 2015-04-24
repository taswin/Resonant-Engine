package com.resonant.core.graph.internal.electric.component

import com.resonant.core.graph.internal.electric.NodeElectricJunction

/**
 * Essentially a wrapper to collapse wires into junctions for easy management.
 * @author Calclavia
 */
class Junction {
	/**
	 * The electric potential at this junction.
	 */
	var voltage = 0d

	/**
	 * The wires that collapsed into this junction
	 */
	var wires = Set.empty[NodeElectricJunction]

	/**
	 * The total resistance of this junction due to wires
	 */
	def resistance = wires.map(_.resistance).foldLeft(0d)(_ + _)
}
