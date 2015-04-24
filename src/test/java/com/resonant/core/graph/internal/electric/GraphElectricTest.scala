package com.resonant.core.graph.internal.electric

import java.util

import com.resonant.core.graph.api.NodeElectric
import nova.core.util.Direction
import org.junit.Assert._
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
	 * Simplest circuit.
	 * Testing adjacency matrix and voltage at each resistor.
	 */
	@Test
	def testSolve1() {
		/**
		 * The most simple circuit
		 */
		val profilerGen = new Profiler("Generate graph 1")

		val graph = new GraphElectric

		val battery = new DummyComponent()
		val wire1 = new DummyWire()
		val resistor1 = new DummyComponent()
		val wire2 = new DummyWire()

		battery.connectNeg(wire2)
		val components = connectInSeries(battery, wire1, resistor1, wire2)
		wire2.connect(battery)

		components.foreach(graph.add)
		println(profilerGen)

		val profilerAdj = new Profiler("Building adjacency for graph 1")
		graph.buildAll()
		println(profilerAdj)

		println(graph.adjMat)
		//Test component & junction sizes
		assertEquals(2, graph.components.size)
		//There should be one less junction in the list, due to the ground
		assertEquals(1, graph.junctions.size)
		//Test forward connections
		assertEquals(true, graph.adjMat(battery, wire1))
		assertEquals(true, graph.adjMat(wire1, resistor1))
		assertEquals(true, graph.adjMat(resistor1, wire2))
		assertEquals(true, graph.adjMat(wire2, battery))
		//Test getDirectedTo connections
		assertEquals(Set(wire1, wire2), graph.adjMat.getDirectedTo(battery))
		assertEquals(Set(battery), graph.adjMat.getDirectedTo(wire1))
		assertEquals(Set(wire1, wire2), graph.adjMat.getDirectedTo(resistor1))
		assertEquals(Set(resistor1), graph.adjMat.getDirectedTo(wire2))
		//Test getDirectedFrom connections
		assertEquals(Set(wire1), graph.adjMat.getDirectedFrom(battery))
		assertEquals(Set(battery, resistor1), graph.adjMat.getDirectedFrom(wire1))
		assertEquals(Set(wire2), graph.adjMat.getDirectedFrom(resistor1))
		assertEquals(Set(resistor1, battery), graph.adjMat.getDirectedFrom(wire2))

		graph.buildAll()

		val profiler = new Profiler("Solving graph 1")

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			battery.genVoltage = voltage
			graph.solveAll()

			//Test battery
			assertEquals(voltage, battery.voltage, 0.0001)
			assertEquals(voltage, battery.current, 0.0001)
			//Test resistor
			assertEquals(voltage, resistor1.voltage, 0.0001)
			assertEquals(voltage, resistor1.current, 0.0001)
			profiler.lap()
		}

		profiler.printAverage()
	}

	/**
	 * Graph 2.
	 * Series circuit with more than one node.
	 */
	@Test
	def testSolve2() {
		val profilerGen = new Profiler("Generate graph 2")

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
		println(profilerGen)

		graph.buildAll()
		val profiler = new Profiler("Solving graph 2")

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			battery.genVoltage = voltage
			graph.solveAll()

			val current = voltage / 3d
			//Test battery
			assertEquals(voltage, battery.voltage, 0.0001)
			assertEquals(current, battery.current, 0.0001)
			//Test resistor1
			assertEquals(voltage / 3, resistor1.voltage, 0.0001)
			assertEquals(current, resistor1.current, 0.0001)
			//Test resistor2
			assertEquals(voltage * 2 / 3, resistor2.voltage, 0.0001)
			assertEquals(current, resistor2.current, 0.0001)
			profiler.lap()
		}

		profiler.printAverage()
	}

	/**
	 * Graph 3.
	 * Parallel circuit with more than one node and employing virtual junctions.
	 * |-- -|+ ---|
	 * |          |
	 * |--||---||-|
	 * |          |
	 * |---||-||--|
	 */
	//TODO: Make string to circuit converter. :)
	@Test
	def testSolve3() {
		val profilerGen = new Profiler("Generate graph 3")

		val graph = new GraphElectric

		val battery = new DummyComponent()
		val wire1 = new DummyWire()
		val wire2 = new DummyWire()
		val resistor1 = new DummyComponent()
		val wire3 = new DummyWire()
		val resistor2 = new DummyComponent()
		resistor2.setResistance(2)
		val wire4 = new DummyWire()
		val wire5 = new DummyWire()
		val resistor3 = new DummyComponent()
		resistor3.setResistance(3)
		val resistor4 = new DummyComponent()

		battery.connectNeg(wire4)
		val seriesA = connectInSeries(battery, wire1, wire2, resistor1, wire3, resistor2, wire4)
		wire4.connect(battery)
		val seriesB = connectInSeries(wire2, resistor3, resistor4, wire4)

		seriesA.foreach(graph.add)
		seriesB.foreach(graph.add)

		println(profilerGen)

		graph.buildAll()
		val profiler = new Profiler("Solving graph 3")

		//Using 1/R = 1/R1+1/R2+...
		val totalResistance = 12 / 7d

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			battery.genVoltage = voltage
			graph.solveAll()

			//Test battery
			assertEquals(voltage, battery.voltage, 0.0001)
			assertEquals(voltage / totalResistance, battery.current, 0.0001)

			//Branch A:
			val currentA = voltage / 3d
			//Test resistor1
			assertEquals(voltage / 3, resistor1.voltage, 0.0001)
			assertEquals(currentA, resistor1.current, 0.0001)
			//Test resistor2
			assertEquals(voltage * 2 / 3, resistor2.voltage, 0.0001)
			assertEquals(currentA, resistor2.current, 0.0001)

			//Branch B:
			val currentB = voltage / 4d
			//Test resistor1
			assertEquals(voltage / 4, resistor1.voltage, 0.0001)
			assertEquals(currentB, resistor1.current, 0.0001)
			//Test resistor2
			assertEquals(voltage * 3 / 4, resistor2.voltage, 0.0001)
			assertEquals(currentB, resistor2.current, 0.0001)

			profiler.lap()
		}

		profiler.printAverage()
	}

	/**
	 * A complex circuit with multiple batteries
	 *
	 * |-|||- -|+ ------|
	 * |                |
	 * |----- -|+ --|||-|
	 * |                |
	 * |------||||------|
	 */
	@Test
	def testSolve4() {
		val profilerGen = new Profiler("Generate graph 3")

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

		println(profilerGen)

		graph.buildAll()
		val profiler = new Profiler("Solving graph 3")

		for (trial <- 1 to 1000) {
			val voltage = trial * 10d * Math.random()
			battery1.genVoltage = voltage
			battery2.genVoltage = voltage
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
