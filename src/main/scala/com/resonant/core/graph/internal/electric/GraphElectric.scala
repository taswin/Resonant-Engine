package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.internal.GraphConnect
import com.resonant.core.graph.internal.electric.component.{Junction, VirtualJunction}
import com.resonant.core.prefab.block.Updater
import nova.core.game.Game
import nova.core.util.transform.Matrix

import scala.collection.JavaConversions._

/**
 * An electric circuit grid for independent voltage sources.
 *
 * @author Calclavia
 */
class GraphElectric extends GraphConnect[NodeElectricComponent] with Updater {

	// There should always at least (node.size - 1) amount of junctions.
	var junctions = Seq.empty[Junction]
	var voltageSources = Seq.empty[NodeElectricComponent]
	var resistors = Seq.empty[NodeElectricComponent]

	/**
	 * Reconstruct must build the links and intersections of the grid
	 */
	override def build() {
		/**
		 * Builds the adjacency matrix.
		 * The directed graph indicate current flow from positive terminal to negative terminal.
		 */
		adjMat = Array.ofDim[Boolean](nodes.size, nodes.size)

		for (node <- nodes) {
			for (con <- node.positives) {
				if (nodes.contains(con)) {
					connect(node, con.asInstanceOf[NodeElectricComponent])
				}
			}
		}

		solveJunctions()
		solveGraph()
		Game.instance.syncTicker.add(this)
	}

	/**
	 * Collapse all wires into junctions (conventionally called nodes).
	 */
	private def solveJunctions() {
		/**
		 * Finds all the wire nodes connected to this one.
		 */
		def recurseFind(wire: NodeElectricJunction, result: Set[NodeElectricJunction] = Set.empty): Set[NodeElectricJunction] = {
			val wireConnections = wire.connections.filter(_.isInstanceOf[NodeElectricJunction]).map(_.asInstanceOf[NodeElectricJunction])
			var newResult = result + wire

			newResult ++= wireConnections
				.filterNot(result.contains)
				.map(n => recurseFind(n, newResult))
				.flatten

			return newResult
		}

		var recursed = Set.empty[NodeElectricComponent]
		val relevantNodes = nodes.filter(_.isInstanceOf[NodeElectricJunction]).map(_.asInstanceOf[NodeElectricJunction])

		for (node <- relevantNodes) {
			if (!recursed.contains(node)) {
				//Create a junction
				val junction = new Junction
				val foundWires = recurseFind(node).toSet[NodeElectricComponent]
				recursed ++= foundWires
				junction.wires = foundWires
				junction.components = foundWires
					.map(_.connections)
					.flatten
					.filterNot(_.isInstanceOf[NodeElectricJunction])
					.map(_.asInstanceOf[NodeElectricComponent])

				foundWires.foreach(
					w => {
						w.junctionNegative = junction
						w.junctionPositive = junction
					}
				)
				junctions :+= junction

				//TODO: Create virtual junctions with resistors to simulate wire resistance
			}
		}
	}

	/**
	 * Populates the node and junctions recursively
	 * TODO: Unit test the grid population algorithm
	 */
	private def solveGraph() {
		var recursed = Set.empty[NodeElectricComponent]

		def solveGraph(node: NodeElectricComponent, prev: NodeElectricComponent = null) {
			//Check if we already traversed through this node and if it is valid. Proceed if we haven't already done so.
			if (!recursed.contains(node)) {
				//Add this node into the list of nodes.
				recursed += node

				//If the node has at least a positive and negative connection, we can build two junctions across it.
				if (node.positives.size > 0 && node.negatives.size > 0) {
					//Use the junction that this node came from. If this is the first node being recursed, then create a new junction.
					node.junctionNegative = {
						if (prev == null) {
							/**
							 * This is only called in the first recursion loop.
							 *
							 * Look through all junctions, see if there is already one that is connected to this node, but NOT the previous junction
							 * If the junction does NOT exist, then create a new virtual one
							 */
							junctions.find(j => j.components.contains(node) && (j.wires.exists(node.positives.contains) || (node.dynamicTerminals && j.wires.exists(node.negatives.contains)))) match {
								case Some(j) => j //We found another wire to connect to
								case _ =>
									//We create a new virtual junction, because this terminal is not connected to any wires, but another component
									val virtual = new VirtualJunction
									virtual.components += node
									virtual
							}
						}
						else {
							prev.junctionPositive
						}
					}

					//Create a new junction for junction B
					node.junctionPositive = {
						/**
						 * Look through all junctions, see if there is already one that is connected to this node.
						 * If the junction does NOT exist, then create a new virtual one
						 */
						junctions.find(j => j.components.contains(node) && node.junctionNegative != j) match {
							case Some(j) => j //We found another wire to connect to
							case _ =>
								junctions.find(j => j.isInstanceOf[VirtualJunction] && j.components.contains(node) && node.junctionNegative != j) match {
									case Some(j) =>
										j.components += node
										j //We found another virtual junction to connect to
									case _ =>
										//We create a new virtual junction, because this terminal is not connected to any wires, but another component
										val virtual = new VirtualJunction
										virtual.components += node
										virtual.components ++= node.connections.filterNot(_.isInstanceOf[NodeElectricJunction]).map(_.asInstanceOf[NodeElectricComponent])
										virtual
								}
						}
					}

					junctions :+= node.junctionNegative
					junctions :+= node.junctionPositive

					//Recursively populate for all nodes connected to junction B, because junction A simply goes backwards in the graph. There is no point iterating it.
					node.junctionPositive.nodes.foreach(next => solveGraph(next, node))
				}
			}
		}

		nodes.filterNot(_.isInstanceOf[NodeElectricJunction]).headOption match {
			case Some(x) => solveGraph(x)
			case _ =>
		}
	}

	override def update(deltaTime: Double) {
		/**
		 * Solve circuit using MNA, based on http://www.swarthmore.edu/NatSci/echeeve1/Ref/mna/MNA3.html
		 * We will be solving systems of linear equations using matrices.
		 */
		//TODO: The A matrix only should change when resistance changes
		//TODO: The b matrix only changes when voltage or current sources change

		val n = junctions.size
		val m = voltageSources.size
		val mnaMat = new Matrix(n + m)

		//Construct G sub-matrix
		//Set all diagonals of the nxn part of the matrix with the sum of its adjacent resistor's conductance
		for (i <- 0 until n) {
			mnaMat(i, i) = junctions(i).components.map(1 / _.resistance).sum
		}

		//Set all resistors to have its conductance
		for (resistor <- resistors) {
			val i = junctions.indexOf(resistor.junctionNegative)
			val j = junctions.indexOf(resistor.junctionPositive)
			val negConductance = -1 / resistor.resistance
			mnaMat(i, j) = negConductance
			mnaMat(j, i) = negConductance
		}

		/**
		 * Construct B nxm and C mxn sub-matrix, with only 0, 1, and -1 elements.
		 * The C matrix is the transpose of B matrix.
		 * The B matrix is an nxm matrix with only 0, 1 and -1 elements.
		 * Each location in the matrix corresponds to a particular voltage source (first dimension) or a node (second dimension).
		 * If the positive terminal of the ith voltage source is connected to node k, then the element (i,k) in the B matrix is a 1.
		 * If the negative terminal of the ith voltage source is connected to node k, then the element (i,k) in the B matrix is a -1.
		 * Otherwise, elements of the B matrix are zero.
		 */
		//TODO: Matrix B and C only change when grid is rebuilt
		for ((voltageSource, i) <- voltageSources.zipWithIndex) {
			//Positive terminal
			val posIndex = junctions.indexOf(voltageSource.junctionPositive)
			mnaMat(n + i, posIndex) = 1
			mnaMat(posIndex, n + i) = 1
			//Negative terminal
			val negIndex = junctions.indexOf(voltageSource.junctionNegative)
			mnaMat(n + i, negIndex) = -1
			mnaMat(negIndex, n + i) = -1
			//TODO: Check if transpose is correct.
		}

		//The b matrix is a column vector, the right hand side of Ax = b equation.
		val b = new Matrix(n + m, 1)

		//There are no current sources
		/*for (i <- 0 until n)
		{
			b(i, 0) = 0
		}*/

		for (i <- n until m) {
			b(i, 0) = voltageSources(i - n).bufferVoltage
		}

		//Solve the circuit
		val x = mnaMat.solve(b)

		//Set the voltage at the nodes
		for (i <- 0 until n) {
			junctions(i).voltage = x(i, 0)
		}

		//Set the current of the voltage sources
		for (i <- n until m) {
			voltageSources(i - n).current = x(i, 0)
		}
	}
}