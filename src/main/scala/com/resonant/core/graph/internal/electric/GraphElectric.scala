package com.resonant.core.graph.internal.electric

import com.resonant.core.graph.api.NodeElectric
import com.resonant.core.graph.internal.GraphConnect
import com.resonant.core.graph.internal.electric.component.{Junction, VirtualJunction}
import com.resonant.core.prefab.block.Updater
import com.resonant.lib.math.matrix.AdjacencyMatrix
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
	//The component-junction matrix. Rows are from, columns are to. In the directed graph the arrow points from positive to negative in potential difference.
	protected[graph] var terminalMatrix: AdjacencyMatrix[AnyRef] = null

	/**
	 * Reconstruct must build the links and intersections of the grid
	 */
	override def build() {

		buildAll()
		Game.instance.syncTicker.add(this)
	}

	def buildAll() {
		/**
		 * Clean all variables
		 */
		junctions = List.empty
		ground = null
		components = List.empty
		mnaMat = null
		sourceMatrix = null
		terminalMatrix = null

		/**
		 * Builds the adjacency matrix.
		 * The directed graph indicate current flow from positive terminal to negative terminal.
		 */
		adjMat = new AdjacencyMatrix(nodes, nodes)

		var recursedWires = Set.empty[NodeElectricJunction]
		//A queue of virtual junctions to their corresponding tuple component to be binded together
		var virtualBindQueue = Map.empty[VirtualJunction, (NodeElectricComponent, NodeElectricComponent)]

		nodes.foreach {
			case node: NodeElectricComponent =>
				for (con <- node.positives) {
					if (nodes.contains(con)) {
						con match {
							case component: NodeElectricComponent =>
								//Check if the "component" is negatively connected to the current node
								if (component.negatives.contains(node)) {
									adjMat(node, component) = true
									//This component is connected to another component. Create virtual junctions between them.
									var junction = new VirtualJunction
									virtualBindQueue += junction ->(node, component)
									junctions :+= junction
								}
							case junction: NodeElectricJunction =>
								adjMat(node, junction) = true
						}
					}
				}
			case node: NodeElectricJunction =>
				for (con <- node.connections()) {
					if (nodes.contains(con)) {
						adjMat(node, con) = true
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
		 * Create the connect adjacency matrix.
		 */
		terminalMatrix = new AdjacencyMatrix[AnyRef](nodes ++ junctions ++ virtualBindQueue.keys)

		junctions.foreach {
			case virtualJunction: VirtualJunction =>
				val (a, b) = virtualBindQueue(virtualJunction)
				terminalMatrix(a, virtualJunction) = true
				terminalMatrix(virtualJunction, b) = true
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
					if (adjMat.getDirectedFrom(component).exists(c => junction.wires.contains(c))) {
						//Component is connected to junction via positive terminal
						terminalMatrix(component, junction) = true
					}
					else if (adjMat.getDirectedTo(component).exists(c => junction.wires.contains(c))) {
						//Component is connected to junction via negative terminal
						terminalMatrix(junction, component) = true
					}
				})
		}

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
						.filter(resistor => terminalMatrix.isConnected(resistor, junction))
						.map(1 / _.resistance)
						.sum
			}

			//The off diagonal elements are the negative conductance of the element connected to the pair of corresponding node.
			//Therefore a resistor between nodes 1 and 2 goes into the G matrix at location (1,2) and locations (2,1).
			for (resistor <- resistors) {
				//The id of the junction at negative terminal
				val i = junctions.indexOf(terminalMatrix.getDirectedTo(resistor).head)
				//The id of the junction at positive terminal
				val j = junctions.indexOf(terminalMatrix.getDirectedFrom(resistor).head)

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
					val posIndex = junctions.indexOf(terminalMatrix.getDirectedFrom(voltageSource).head)
					//Check to make sure this is not the ground reference junction
					if (posIndex != -1) {
						mnaMat(n + i, posIndex) = 1
						mnaMat(posIndex, n + i) = 1
					}
					//Negative terminal
					val negIndex = junctions.indexOf(terminalMatrix.getDirectedTo(voltageSource).head)
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
						(adjMat.getDirectedTo(source).contains(junctions(i)) && source.current > 0) || (adjMat.getDirectedFrom(source).contains(junctions(i)) && source.current < 0)
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
			case (component, index) =>
				val wireTo = terminalMatrix.getDirectedTo(component).head.asInstanceOf[Junction]
				val wireFrom = terminalMatrix.getDirectedFrom(component).head.asInstanceOf[Junction]
				component.voltage = wireFrom.voltage - wireTo.voltage
				component.current = component.voltage / component.resistance
		}
	}

	override def update(deltaTime: Double) {
		solveAll()
	}
}