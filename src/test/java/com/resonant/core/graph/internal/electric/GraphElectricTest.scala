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
		val components = connectInSeries(battery, wire1, resistor1, wire2)
		wire2.connect(battery)

		battery.generateVoltage(12)

		components.foreach(graph.add)
		println(profiler)

		return graph
	}

	/**
	 * A simple series circuit
	 */
	def generateSeriesCircuit2: GraphElectric = {
		val profiler = new Profiler("Generate graph 2")

		val graph = new GraphElectric

		val battery = new DummyComponent()
		val wire1 = new DummyWire()
		val wire2 = new DummyWire()
		val resistor1 = new DummyComponent()
		val wire3 = new DummyWire()
		val resistor2 = new DummyComponent()
		resistor2.setResistance(2)
		val wire4 = new DummyWire()

		battery.connectNeg(wire4)
		val components = connectInSeries(battery, wire1, wire2, resistor1, wire3, resistor2, wire4)
		wire4.connect(battery)

		battery.generateVoltage(6)

		components.foreach(graph.add)
		println(profiler)

		return graph
	}

	/**
	 * Connects a sequence of electric nodes in series excluding the first and last connection.
	 */
	def connectInSeries(series: NodeElectric*): Seq[NodeElectric] = {
		series.zipWithIndex.foreach {
			case (component: DummyComponent, index) =>
				index match {
					case 0 => component.connectPos(series(index + 1))
					case l if l == series.size - 1 =>
						component.connectNeg(series(index - 1))
					case _ =>
						component.connectNeg(series(index - 1))
						component.connectPos(series(index + 1))
				}
			case (wire: DummyWire, index) =>
				index match {
					case 0 => wire.connect(series(index + 1))
					case l if l == series.size - 1 =>
						wire.connect(series(index - 1))
					case _ =>
						wire.connect(series(index - 1))
						wire.connect(series(index + 1))
				}
		}
		return series
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
	def testSolve1() {
		/**
		 * Graph 1
		 */
		val graph1 = generateSeriesCircuit1
		graph1.buildAdjacency()

		val profiler = new Profiler("Solving graph 1")

		for (trial <- 1 to 100) {
			val voltage = trial * 10
			graph1.getNodes.get(0).asInstanceOf[NodeElectricComponent].genVoltage = voltage
			graph1.solveAll()

			//Test battery
			assertEquals(voltage, graph1.getNodes.get(0).voltage, 0.0001)
			assertEquals(voltage, graph1.getNodes.get(0).current, 0.0001)
			//Test resistor
			assertEquals(voltage, graph1.getNodes.get(2).voltage, 0.0001)
			assertEquals(voltage, graph1.getNodes.get(2).current, 0.0001)

			println(profiler)
			profiler.lap()
		}

		profiler.printAverage()
	}

	@Test
	def testSolve2() {
		/**
		 * Graph 2
		 */
		val graph2 = generateSeriesCircuit2
		graph2.buildAdjacency()

		val profiler2 = new Profiler("Solving graph 2")
		graph2.solveAll()
		println(profiler2)

		//Test battery
		assertEquals(6, graph2.getNodes.get(0).voltage, 0.0001)
		assertEquals(2, graph2.getNodes.get(0).current, 0.0001)
		//Test resistor1
		assertEquals(2, graph2.getNodes.get(2).voltage, 0.0001)
		assertEquals(2, graph2.getNodes.get(2).current, 0.0001)
		//Test resistor2
		assertEquals(4, graph2.getNodes.get(2).voltage, 0.0001)
		assertEquals(2, graph2.getNodes.get(2).current, 0.0001)
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
		var lapped = Seq.empty[Long]

		def lap() {
			lapped :+= System.currentTimeMillis() - time
			time = System.currentTimeMillis()
		}

		override def toString = name + " took " + ((System.currentTimeMillis() - time) / 1000d) + " seconds"

		def average = lapped.map(_ / 1000d).sum / lapped.size

		def printAverage(): Unit = {
			println(name + " took " + average + " seconds on average")
		}
	}

}
