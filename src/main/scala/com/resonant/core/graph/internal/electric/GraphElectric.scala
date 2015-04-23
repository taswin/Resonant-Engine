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
	var junctions = List.empty[Junction]
	//The reference ground junction where voltage is zero.
	var ground: Junction = null
	//The components in the circuit
	var components = List.empty[NodeElectricComponent]

	//The modified nodal analysis matrix (A) in Ax=b linear equation.
	protected[graph] var mnaMat: Matrix = null
	//The source matrix (B)
	protected[graph] var sourceMatrix: Matrix = null

	/**
	 * Reconstruct must build the links and intersections of the grid
	 */
	override def build() {
		buildAll()
		Game.instance.syncTicker.add(this)
	}

	def buildAll() {
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
		junctions.foreach {
			case junction =>
				//Find all the components connected to this junction
				val connectedComponents = junction.wires
					.flatMap(_.connections)
					.collect { case n: NodeElectricComponent => n }

				//Add the found components to the global components list
				connectedComponents
					.filterNot(components.contains)
					.foreach(components :+= _)

				//Set adjMat connection by marking the component-junction position as true
				connectedComponents.foreach(component => {
					//TODO: Use the adjMat instead to speed up
					if (component.positives.exists(c => junction.wires.contains(c))) {
						component.junctionPositive = junction
					}
					else if (component.negatives.exists(c => junction.wires.contains(c))) {
						component.junctionNegative = junction
					}
				})
		}

		//Some nodes are connected to other nodes instead of wires. We need to create virtual nodes and place them between the junctions!

		//Select reference ground
		ground = junctions.head
		junctions = junctions.splitAt(1)._2
		ground.voltage = 0
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
		val n = junctions.size
		val m = voltageSources.size

		/**
		 * Setup MNA Matrix
		 */
		mnaMat = new Matrix(n + m)

		generateConductanceMatrix()
		generateConnectionMatrix()

		def generateConductanceMatrix() {
			//Construct G sub-matrix
			//Set all diagonals of the nxn part of the matrix with the sum of its adjacent resistor's conductance
			junctions.zipWithIndex.foreach {
				case (junction, i) =>
					mnaMat(i, i) = resistors
						.filter(resistor => resistor.junctionPositive == junction || resistor.junctionNegative == junction)
						.map(1 / _.resistance)
						.sum
			}

			//The off diagonal elements are the negative conductance of the element connected to the pair of corresponding node.
			//Therefore a resistor between nodes 1 and 2 goes into the G matrix at location (1,2) and locations (2,1).
			for (resistor <- resistors) {
				//The id of the junction at negative terminal
				val i = junctions.indexOf(resistor.junctionNegative)
				//The id of the junction at positive terminal
				val j = junctions.indexOf(resistor.junctionPositive)

				//Check to make sure this is not the ground reference junction
				if (i != -1 && j != -1) {
					val negConductance = -1 / resistor.resistance
					mnaMat(i, j) = negConductance
					mnaMat(j, i) = negConductance
				}
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
			voltageSources.zipWithIndex.foreach {
				case (voltageSource, i) =>
					//Positive terminal
					val posIndex = junctions.indexOf(voltageSource.junctionPositive)
					//Check to make sure this is not the ground reference junction
					if (posIndex != -1) {
						mnaMat(n + i, posIndex) = 1
						mnaMat(posIndex, n + i) = 1
					}
					//Negative terminal
					val negIndex = junctions.indexOf(voltageSource.junctionNegative)
					//Check to make sure this is not the ground reference junction
					if (negIndex != -1) {
						mnaMat(n + i, negIndex) = -1
						mnaMat(negIndex, n + i) = -1
					}
			}
		}

		/**
		 * The source matrix is a column vector, the right hand side of Ax = b equation.
		 * It contains two parts.
		 */
		//TODO: Only when sources change
		def computeSourceMatrix() {
			sourceMatrix = new Matrix(n + m, 1)

			//Part one: The sum of current sources corresponding to a particular node
			for (i <- 0 until n) {
				//A set of current sources that is going into this junction
				sourceMatrix(i, 0) = currentSources.filter(
					source =>
						(adjMat.getDirectedTo(nodes.indexOf(source)).contains(junctions(i)) && source.current > 0) || (adjMat.getDirectedFrom(nodes.indexOf(source)).contains(junctions(i)) && source.current < 0)
				)
					.map(_.current)
					.sum
			}

			//Part two: The voltage of each voltage source
			for (i <- 0 until m) {
				sourceMatrix(i + n, 0) = voltageSources(i).genVoltage
			}
		}

		computeSourceMatrix()
		//TODO: Recalculation is only required when parts of circuit changes

		//Solve the circuit
		//TODO: Check why negation is required?
		val x = mnaMat.solve(sourceMatrix * -1)

		//Retrieve the voltage of the junctions
		for (i <- 0 until n) {
			junctions(i).voltage = x(i, 0)
		}

		//Retrieve the current values of the voltage sources
		for (i <- 0 until m) {
			voltageSources(i).voltage = voltageSources(i).genVoltage
			voltageSources(i).current = x(i + n, 0)
		}

		//Calculate the potential difference for each component based on its junctions
		resistors.zipWithIndex.foreach {
			case (components, index) =>
				val wireTo = nodes(adjMat.getDirectedTo(nodes.indexOf(components)).head)
				val wireFrom = nodes(adjMat.getDirectedFrom(nodes.indexOf(components)).head)

				val voltageIn = junctions.find(_.wires.contains(wireTo)).headOption match {
					case Some(junction) => junction.voltage
					case _ => 0 //Ground
				}
				val voltageOut = junctions.find(_.wires.contains(wireFrom)).headOption match {
					case Some(junction) => junction.voltage
					case _ => 0 //Ground
				}

				components.voltage = voltageIn - voltageOut
				components.current = components.voltage / components.resistance
		}
	}

	override def update(deltaTime: Double) {
		solveAll()
	}
}