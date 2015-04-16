package com.resonant.core.graph.internal.electric.component

import com.resonant.core.graph.internal.electric.NodeElectricComponent

/**
 * @author Calclavia
 */
class Junction {
	/**
	 * The electric potential at this junction.
	 */
	var voltage = 0d

	/**
	 * The components that this junction is connected to.
	 */
	var components = Set.empty[NodeElectricComponent]

	/**
	 * The wires that collapsed into this junction
	 */
	var wires = Set.empty[NodeElectricComponent]

	/**
	 * The total resistance of this junction due to wires
	 */
	def resistance = wires.map(_.resistance).foldLeft(0d)(_ + _)
}
