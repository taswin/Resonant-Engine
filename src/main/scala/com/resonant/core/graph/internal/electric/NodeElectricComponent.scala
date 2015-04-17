package com.resonant.core.graph.internal.electric

import java.util.{Set => JSet}

import com.resonant.core.graph.api.{NodeElectric, NodeProvider}
import com.resonant.core.graph.internal.electric.component.Junction
import com.resonant.wrapper.lib.wrapper.BitmaskWrapper._
import nova.core.util.Direction

import scala.collection.convert.wrapAll._

/**
 * Represents an electric component in a circuit.
 *
 * An electric component must be in between two junctions in order to function.
 *
 * Flow of current should be positive when current from junction A is flowing to B
 *
 * @author Calclavia
 */
class NodeElectricComponent(parent: NodeProvider) extends NodeAbstractElectric(parent) {
	/**
	 * When dynamic terminal is set to true, then the grid will attempt to swap negative and positive terminals as needed.
	 */
	var dynamicTerminals = false

	/**
	 * The current and voltage values are set are determined by the DC Grid
	 */
	var voltage = 0d

	var current = 0d

	protected[electric] var junctionPositive: Junction = null
	protected[electric] var junctionNegative: Junction = null

	/**
	 * Variables to keep voltage source states
	 */
	protected[electric] var genVoltage = 0d
	protected[electric] var genCurrent = 0d

	/**
	 * The positive terminals are the directions in which charge can flow out of this electric component.
	 * Positive and negative terminals must be mutually exclusive.
	 *
	 * The mask is a 6 bit data each storing a specific side value
	 */
	private var positiveMask = 0
	/**
	 * The negative terminals are the directions in which charge can flow into this electric component.
	 * Positive and negative terminals must be mutually exclusive.
	 *
	 * The mask is a 6 bit data each storing a specific side value
	 */
	private var negativeMask = 0

	def positives: JSet[NodeElectric] = connectedMap.filter(keyVal => positiveMask.mask(keyVal._2)).keySet

	def negatives: JSet[NodeElectric] = connectedMap.filter(keyVal => negativeMask.mask(keyVal._2)).keySet

	def setPositive(dir: Direction, open: Boolean = true) {
		positiveMask = positiveMask.mask(dir, open)
		negativeMask &= ~positiveMask
		connectionMask = positiveMask | negativeMask
	}

	def setPositives(dirs: JSet[Direction]) {
		positiveMask = 0

		dirs.foreach(dir => positiveMask = positiveMask.mask(dir, true))
		negativeMask &= ~positiveMask
		connectionMask = positiveMask | negativeMask
	}

	def setNegative(dir: Direction, open: Boolean = true) {
		negativeMask = negativeMask.mask(dir, open)
		positiveMask &= ~negativeMask
		connectionMask = positiveMask | negativeMask
	}

	def setNegatives(dirs: JSet[Direction]) {
		negativeMask = 0

		dirs.foreach(dir => negativeMask = negativeMask.mask(dir, true))
		positiveMask &= ~negativeMask
		connectionMask = positiveMask | negativeMask
	}

	/**
	 * Generates a potential difference across the two intersections that go across this node.
	 * @param voltage - The target voltage, in Volts
	 */
	def generateVoltage(voltage: Double) {
		genVoltage = voltage
	}

	/**
	 * Generates power by adjusting varying the voltage until the target power is reached
	 * @param power - The target power, in Watts
	 */
	def generatePower(power: Double) {
		genCurrent = power
	}

	override def toString = "ElectricComponent [" + connections.size() + " " + BigDecimal(current).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A " + BigDecimal(voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"
}
