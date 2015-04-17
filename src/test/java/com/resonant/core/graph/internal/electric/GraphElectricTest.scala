package com.resonant.core.graph.internal.electric

import java.util

import com.resonant.core.graph.api.NodeElectric
import com.resonant.core.graph.internal.AdjacencyMatrix
import nova.core.util.Direction
import org.junit.Assert.assertEquals
import org.junit.Test

import scala.collection.convert.wrapAll._
import scala.collection.mutable

/**
 * @author Calclavia
 */
class GraphElectricTest {

	/**
	 * The most simple circuit
	 */
	def generateSeriesCircuit1: GraphElectric = {
		val profiler = new Profiler("Generate graph 1")

		val graph = new GraphElectric

		val battery = new DummyComponent()
		val wire1 = new DummyWire()
		val resistor1 = new DummyComponent()
		val wire2 = new DummyWire()

		battery.connectNeg(wire2)
		battery.connectPos(wire1)
		wire1.connect(battery)
		wire1.connect(resistor1)
		resistor1.connectNeg(wire1)
		resistor1.connectPos(wire2)
		wire2.connect(battery)
		wire2.connect(resistor1)

		battery.generateVoltage(12)

		graph.add(battery)
		graph.add(wire1)
		graph.add(resistor1)
		graph.add(wire2)
		println(profiler)
		return graph
	}

	@Test
	def testBuild() {
		val graph = generateSeriesCircuit1

		val profiler = new Profiler("Building adjacency for graph 1")
		graph.buildAdjacency()
		println(profiler)

		val compareComponentJunctionMat = new AdjacencyMatrix(2, 2)
		compareComponentJunctionMat(0, 0) = true
		compareComponentJunctionMat(0, 1) = true
		compareComponentJunctionMat(1, 0) = true
		compareComponentJunctionMat(1, 1) = true

		println(graph.adjMat)
		assertEquals(graph.componentJunctionMat, compareComponentJunctionMat)
		assertEquals(graph.junctions.size, 2)
	}

	@Test
	def testSolveComponents() = {
		val graph = generateSeriesCircuit1
		graph.buildAdjacency()

		val profiler = new Profiler("Solving graph 1")
		graph.solveAll()
		println(profiler)

		//Test battery
		assertEquals(12, graph.getNodes.get(0).voltage, 0.0001)
		assertEquals(12, graph.getNodes.get(0).current, 0.0001)
		//Test resistor
		assertEquals(12, graph.getNodes.get(2).voltage, 0.0001)
		assertEquals(12, graph.getNodes.get(2).current, 0.0001)
	}

	class DummyComponent extends NodeElectricComponent(null) {
		connectedMap = mutable.Map.empty[NodeElectric, Direction]

		def connectPos(nodeElectricComponent: NodeElectric) {
			connectedMap += (nodeElectricComponent -> Direction.NORTH)
			setPositive(Direction.NORTH)
		}

		def connectNeg(nodeElectricComponent: NodeElectric) {
			connectedMap += (nodeElectricComponent -> Direction.SOUTH)
			setNegative(Direction.SOUTH)
		}

		override def connections(): util.Set[NodeElectric] = connectedMap.keySet()
	}

	class DummyWire extends NodeElectricJunction(null) {
		connectedMap = mutable.Map.empty[NodeElectric, Direction]

		def connect(nodeElectricComponent: NodeElectric) {
			connectedMap += (nodeElectricComponent -> Direction.UNKNOWN)
		}

		override def connections(): util.Set[NodeElectric] = connectedMap.keySet()
	}

	class Profiler(val name: String) {
		var time = System.currentTimeMillis()

		def lap: this.type = {
			time = System.currentTimeMillis()
			return this
		}

		override def toString = name + " took " + ((System.currentTimeMillis() - time) / 1000d) + " seconds"

	}

}
