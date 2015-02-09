package com.resonant.core.graph.api;

import com.resonant.core.graph.internal.Node;
import nova.core.util.Direction;

import java.util.Set;

/**
 * An electric node
 * @author Calclavia
 */
public interface NodeElectric extends Node<NodeElectric> {

	Set<NodeElectric> positives();

	Set<NodeElectric> negatives();

	void setPositive(Direction dir, boolean open);

	void setPositives(Set<Direction> dirs);

	void setNegative(Direction dir, boolean open);

	void setNegatives(Set<Direction> dirs);

	/**
	 * Gets the current in the component.
	 */
	double current();

	/**
	 * Gets the voltage in the component.
	 */
	double voltage();

	/**
	 * Gets the power in the component.
	 */
	default double power() {
		return current() * voltage();
	}

	/**
	 * Generates a potential difference across the two intersections that go across this node.
	 * @param voltage - The target voltage, in Volts
	 */
	void generateVoltage(double voltage);

	/**
	 * Generates power by adjusting varying the voltage until the target power is reached
	 * @param power - The target power, in Watts
	 */
	void generatePower(double power);
}
