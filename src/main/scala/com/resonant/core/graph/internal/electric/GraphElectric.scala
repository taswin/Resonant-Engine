package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.NodeElectric
import com.resonant.core.graph.internal.electric.component.Junction
import com.resonant.core.graph.internal.{AdjacencyMatrix, GraphConnect}
import com.resonant.core.prefab.block.Updater
import nova.core.game.Game
import nova.core.util.transform.Matrix

import scala.collection.JavaConversions._

/**
 * An electric circuit grid for independent voltage sources.
 *
 * @author Calclavia
 */
//TODO: Move to EDX?
class GraphElectric extends GraphConnect[NodeElectric] with Updater {

	// There should always at least (node.size - 1) amount of junctions.
	var junctions = Seq.empty[Junction]
	var components = Seq.empty[NodeElectricComponent]

	//A matrix that represents connections between junctions and components. If the columns of the matrix forms a basis, then the graph is a complete cycle.
	var componentJunctionMat: AdjacencyMatrix = null

	protected[graph] var mnaMat: Matrix = null
	protected[graph] var sourceMatrix: Matrix = null

	/**
	 * Reconstruct must build the links and intersections of the grid
	 */
	override def build() {

		buildAdjacency()
		solveComponents()
		Game.instance.syncTicker.add(this)
	}

	def buildAdjacency() {
		/**
		 * Builds the adjacency matrix.
		 * The directed graph indicate current flow from positive terminal to negative terminal.
		 */
		adjMat = new AdjacencyMatrix(nodes.size, nodes.size)

		var recursedWires = Set.empty[NodeElectricJunction]

		nodes.foreach {
			case node: NodeElectricComponent =>
				for (con <- node.positives) {
					if (nodes.contains(con)) {
						adjMat(id(node), id(con.asInstanceOf[NodeAbstractElectric])) = true
					}
				}
			case node: NodeElectricJunction =>
				for (con <- node.connections()) {
					if (nodes.contains(con)) {
						adjMat(id(node), id(con.asInstanceOf[NodeAbstractElectric])) = true
					}
				}

				if (!recursedWires.contains(node)) {
					/**
					 * Collapse all wires into junctions.
					 */
					//Create a junction
					val junction = new Junction

					//Find all the wires for this junction
					val foundWires = recurseFind(node)
					//Mark the wire as found, preventing it from generating any new junctions.
					recursedWires ++= foundWires
					junction.wires = foundWires

					//Add to junctions
					foundWires.foreach(_.junction = junction)
					junctions :+= junction

					//TODO: Create virtual junctions with resistors to simulate wire resistance
				}
		}

		/**
		 * Create the component junction adjacency matrix.
		 */
		componentJunctionMat = new AdjacencyMatrix(nodes.size - recursedWires.size, junctions.size)

		junctions.zipWithIndex.foreach {
			case (j, index) =>
				//Find all the components connected to this junction
				val connectedComponents = j.wires
					.flatMap(_.connections)
					.collect { case n: NodeElectricComponent => n }

				//Add the found components to the global components list
				connectedComponents
					.filterNot(components.contains)
					.foreach(components :+= _)

				//Set adjMat connection by marking the component-junction position as true
				connectedComponents
					.map(components.indexOf)
					.foreach(componentJunctionMat(_, index) = true)
		}

	}

	/**
	 * Finds all the interconnected wires that connect to a particular wire.
	 * @param wire The wire to search for.
	 * @return A set of wires that are interconnected.
	 */
	private def recurseFind(wire: NodeElectricJunction, result: Set[NodeElectricJunction] = Set.empty): Set[NodeElectricJunction] = {
		val wireConnections = wire.connections.filter(_.isInstanceOf[NodeElectricJunction]).map(_.asInstanceOf[NodeElectricJunction])
		var newResult = result + wire

		newResult ++= wireConnections
			.filterNot(result.contains)
			.map(n => recurseFind(n, newResult))
			.flatten

		return newResult
	}

	/**
	 * Populates the node and junctions recursively
	 */
	def solveComponents() {
		var recursed = Set.empty[NodeElectricComponent]
		/*
				def solveComponent(node: NodeElectricComponent, prev: NodeElectricComponent = null) {
					//Check if we already traversed through this node and if it is valid. Proceed if we haven't already done so.
					if (!recursed.contains(node)) {
						//Add this node into the set of getNodes.
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

							//Recursively populate for all getNodes connected to junction B, because junction A simply goes backwards in the graph. There is no point iterating it.
							node.junctionPositive.nodes.foreach(next => solveComponent(next, node))
						}
					}
				}

				getNodes.filterNot(_.isInstanceOf[NodeElectricJunction]).headOption match {
					case Some(x) => solveComponent(x)
					case _ =>
				}*/
	}

	/**
	 * Solve circuit using MNA, based on http://www.swarthmore.edu/NatSci/echeeve1/Ref/mna/MNA3.html
	 * We will be solving systems of linear equations using matrices.
	 */
	def solveAll() {
		//TODO: The A matrix only should change when resistance changes
		//TODO: The b matrix only changes when voltage or current sources change

		val voltageSources = components.collect { case source if source.genVoltage != 0 => source }
		val currentSources = components.collect { case source if source.genCurrent != 0 => source }
		val resistors = components diff voltageSources diff currentSources

		/**
		 * Setup MNA Matrix
		 */
		mnaMat = new Matrix(junctions.size + voltageSources.size)

		generateConductanceMatrix()
		generateConnectionMatrix()

		def generateConductanceMatrix() {
			//Construct G sub-matrix
			//Set all diagonals of the nxn part of the matrix with the sum of its adjacent resistor's conductance
			for (i <- 0 until junctions.size) {
				mnaMat(i, i) =
					(0 until voltageSources.size)
						.filter(componentJunctionMat(_)(i))
						.map(voltageSources)
						.map(1 / _.resistance)
						.sum
			}

			//Set all resistors to have its conductance
			for (resistor <- resistors) {
				//The id of the junction at negative terminal
				val i = componentJunctionMat.getDirectedFrom(components.indexOf(resistor)).head
				//The id of the junction at positive terminal
				val j = componentJunctionMat.getDirectedTo(components.indexOf(resistor)).head
				//junctions.indexOf(resistor.junctionPositive)
				val negConductance = -1 / resistor.resistance
				mnaMat(i, j) = negConductance
				mnaMat(j, i) = negConductance
			}
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
		def generateConnectionMatrix() {
			//TODO: Matrix B and C only change when grid is rebuilt
			for ((voltageSource, i) <- voltageSources.zipWithIndex) {
				//Positive terminal
				val posIndex = componentJunctionMat.getDirectedTo(components.indexOf(voltageSource)).head
				mnaMat(junctions.size + i, posIndex) = 1
				mnaMat(posIndex, junctions.size + i) = 1
				//Negative terminal
				val negIndex = componentJunctionMat.getDirectedFrom(components.indexOf(voltageSource)).head
				mnaMat(junctions.size + i, negIndex) = -1
				mnaMat(negIndex, junctions.size + i) = -1
				//TODO: Check if transpose is correct.
			}
		}

		/**
		 * The source matrix is a column vector, the right hand side of Ax = b equation.
		 * It contains two parts.
		 */
		//TODO: Only when sources change
		def computeSourceMatrix() {
			sourceMatrix = new Matrix(junctions.size + voltageSources.size, 1)

			//Part one: The sum of current sources corresponding to a particular node
			for (i <- 0 until junctions.size) {
				//A set of current sources that is going into this junction
				sourceMatrix(i, 0) = currentSources.filter(
					source =>
						(adjMat.getDirectedTo(nodes.indexOf(source)).contains(junctions(i)) && source.current > 0) || (adjMat.getDirectedFrom(nodes.indexOf(source)).contains(junctions(i)) && source.current < 0)
				)
					.map(_.current)
					.sum
			}

			//Part two: The voltage of each voltage source
			for (i <- 0 until voltageSources.size) {
				sourceMatrix(i + junctions.size, 0) = voltageSources(i).genVoltage
			}
		}

		computeSourceMatrix()

		//Solve the circuit
		val x = mnaMat.solve(sourceMatrix)

		//Retrieve the voltage of the junctions
		for (i <- 0 until junctions.size) {
			junctions(i).voltage = x(i, 0)
		}

		//Retrieve the current values of the voltage sources
		for (i <- 0 until voltageSources.size) {
			voltageSources(i).voltage = voltageSources(i).genVoltage
			voltageSources(i).current = x(i + junctions.size, 0)
		}

		//Calculate the potential difference for each component based on its junctions
		resistors.zipWithIndex.foreach {
			case (components, index) =>
				val wireTo = nodes(adjMat.getDirectedTo(nodes.indexOf(components)).head)
				val wireFrom = nodes(adjMat.getDirectedFrom(nodes.indexOf(components)).head)
				components.voltage = junctions.find(_.wires.contains(wireTo)).head.voltage - junctions.find(_.wires.contains(wireFrom)).head.voltage
				components.current = components.voltage / components.resistance
		}
	}

	override def update(deltaTime: Double) {
		solveAll()
	}
}