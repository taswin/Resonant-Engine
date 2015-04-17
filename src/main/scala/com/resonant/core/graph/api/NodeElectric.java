package com.resonant.core.graph.api;

import com.resonant.core.graph.internal.Node;

/**
 * An electric node
 * @author Calclavia
 */
public interface NodeElectric extends Node<NodeElectric> {

	/**
	 * @return The resistance of the electric component in ohms.
	 */
	double resistance();

	/**
	 * @return The voltage (potential difference) of the component in volts.
	 */
	double voltage();

	/**
	 * @return The current of the component in amperes.
	 */
	double current();

	/**
	 * @return The power dissipated in the component.
	 */
	default double power() {
		return current() * voltage();
	}
}
