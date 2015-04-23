package com.resonant.core.graph.internal.electric

import java.util

import com.resonant.core.graph.api.NodeElectric
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

	/**
	 * The most simple circuit
	 */
	def generateCircuit1: GraphElectric = {
		val profiler = new Profiler("Generate graph 1")

		val graph = new GraphElectric

		val battery = new DummyComponent()
		val wire1 = new DummyWire()
		val resistor1 = new DummyComponent()
		val wire2 = new DummyWire()

		battery.connectNeg(wire2)
		val components = connectInSeries(battery, wire1, resistor1, wire2)
		wire2.connect(battery)

		components.foreach(graph.add)
		println(profiler)

		return graph
	}

	@Test
	def testBuild() {
		val graph = generateCircuit1

		val profiler = new Profiler("Building adjacency for graph 1")
		graph.buildAll()
		println(profiler)

		println(graph.adjMat)
		assertEquals(graph.junctions.size, 1)
	}

	@Test
	def testSolve1() {
		/**
		 * Graph 1
		 */
		val graph = generateCircuit1
		graph.buildAll()

		val profiler = new Profiler("Solving graph 1")

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			graph.getNodes.get(0).asInstanceOf[NodeElectricComponent].genVoltage = voltage
			graph.solveAll()

			//Test battery
			assertEquals(voltage, graph.getNodes.get(0).voltage, 0.0001)
			assertEquals(voltage, graph.getNodes.get(0).current, 0.0001)
			//Test resistor
			assertEquals(voltage, graph.getNodes.get(2).voltage, 0.0001)
			assertEquals(voltage, graph.getNodes.get(2).current, 0.0001)
			profiler.lap()
		}

		profiler.printAverage()
	}

	/**
	 * A simple series circuit
	 */
	def generateCircuit2: GraphElectric = {
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

	@Test
	def testSolve2() {
		/**
		 * Graph 2
		 */
		val graph = generateCircuit2
		graph.buildAll()
		val profiler = new Profiler("Solving graph 2")

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			graph.getNodes.get(0).asInstanceOf[NodeElectricComponent].genVoltage = voltage
			graph.solveAll()

			val current = voltage / 3d
			//Test battery
			assertEquals(voltage, graph.getNodes.get(0).voltage, 0.0001)
			assertEquals(current, graph.getNodes.get(0).current, 0.0001)
			//Test resistor1
			assertEquals(voltage / 3, graph.getNodes.get(3).voltage, 0.0001)
			assertEquals(current, graph.getNodes.get(3).current, 0.0001)
			//Test resistor2
			assertEquals(voltage * 2 / 3, graph.getNodes.get(5).voltage, 0.0001)
			assertEquals(current, graph.getNodes.get(5).current, 0.0001)
			profiler.lap()
		}

		profiler.printAverage()
	}

	/**
	 * A complex circuit
	 *
	 * |-|||- -|+ ------|
	 * |                |
	 * |----- -|+ --|||-|
	 * |                |
	 * |------||||------|
	 */
	def generateCircuit3: GraphElectric = {
		val profiler = new Profiler("Generate graph 3")

		val graph = new GraphElectric

		val battery1 = new DummyComponent()
		val battery2 = new DummyComponent()
		val resistor1 = new DummyComponent()
		val resistor2 = new DummyComponent()
		resistor2.setResistance(2)
		val resistor3 = new DummyComponent()
		resistor3.setResistance(2)

		val wire1 = new DummyWire()
		val wire2 = new DummyWire()
		val wire3 = new DummyWire()
		val wire4 = new DummyWire()

		battery1.connectPos(wire1)
		wire1.connect(battery1)
		battery1.connectNeg(wire2)
		wire2.connect(battery1)

		resistor1.connectNeg(wire1)
		wire2.connect(resistor1)
		resistor1.connectPos(wire3)
		wire3.connect(resistor1)

		resistor2.connectPos(wire4)
		wire4.connect(resistor2)
		resistor2.connectNeg(wire1)
		wire1.connect(resistor2)

		battery2.connectPos(wire4)
		wire4.connect(battery2)
		battery2.connectNeg(wire3)
		wire3.connect(battery2)

		resistor3.connectPos(wire3)
		wire3.connect(resistor3)
		resistor3.connectNeg(wire1)
		wire1.connect(resistor3)

		graph.add(battery1)
		graph.add(battery2)
		graph.add(resistor1)
		graph.add(resistor2)
		graph.add(resistor3)
		graph.add(wire1)
		graph.add(wire2)
		graph.add(wire3)
		graph.add(wire4)

		println(profiler)

		return graph
	}

	@Test
	def testSolve3() {
		val graph = generateCircuit3
		graph.buildAll()
		val profiler = new Profiler("Solving graph 3")

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			graph.getNodes.get(0).asInstanceOf[NodeElectricComponent].genVoltage = voltage
			graph.getNodes.get(1).asInstanceOf[NodeElectricComponent].genVoltage = voltage
			graph.solveAll()
			//TODO: Test results
			profiler.lap()
		}

		profiler.printAverage()
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
			println(name + " took " + average + " seconds on average for " + lapped.size + " trials.")
		}
	}

}
