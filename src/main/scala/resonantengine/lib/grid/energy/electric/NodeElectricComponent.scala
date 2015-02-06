package resonantengine.lib.grid.energy.electric

import java.util.{Set => JSet}

import nova.core.util.Direction
import resonantengine.api.graph.INodeProvider
import resonantengine.api.tile.IDebugInfo
import resonantengine.lib.grid.core.{GridNode, NodeGrid, TTileConnector}
import resonantengine.lib.wrapper.BitmaskWrapper._

import scala.beans.BeanProperty
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
class NodeElectricComponent(parent: INodeProvider) extends NodeGrid[NodeElectricComponent](parent) with TTileConnector[NodeElectricComponent] with IDebugInfo
{
  /**
   * When dynamic terminal is set to true, then the grid will attempt to swap negative and positive terminals as needed.
   */
  var dynamicTerminals = false
  /**
   * The current and voltage values are set are determined by the DC Grid
   */
  var voltage = 0d
  var current = 0d
  @BeanProperty
  var resistance = 1d
  /**
   * Variables to keep voltage source states
   */
  protected[electric] var bufferVoltage = 0d
  protected[electric] var bufferPower = 0d
  /**
   * Junction A is always preferably negative
   */
  protected[electric] var junctionA: Junction = null
  /**
   * Junction B is always preferably positive
   */
  protected[electric] var junctionB: Junction = null
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

  def positives: JSet[NodeElectricComponent] = directionMap.filter(keyVal => positiveMask.mask(keyVal._2)).keySet

  def negatives: JSet[NodeElectricComponent] = directionMap.filter(keyVal => negativeMask.mask(keyVal._2)).keySet

	def setPositive(dir: Direction, open: Boolean = true)
  {
    positiveMask = positiveMask.mask(dir, open)
    negativeMask &= ~positiveMask
    connectionMask = positiveMask | negativeMask
  }

	def setPositives(dirs: JSet[Direction])
  {
    positiveMask = 0

    dirs.foreach(dir => positiveMask = positiveMask.mask(dir, true))
    negativeMask &= ~positiveMask
    connectionMask = positiveMask | negativeMask
  }

	def setNegative(dir: Direction, open: Boolean = true)
  {
    negativeMask = negativeMask.mask(dir, open)
    positiveMask &= ~negativeMask
    connectionMask = positiveMask | negativeMask
  }

	def setNegatives(dirs: JSet[Direction])
  {
    negativeMask = 0

    dirs.foreach(dir => negativeMask = negativeMask.mask(dir, true))
    positiveMask &= ~negativeMask
    connectionMask = positiveMask | negativeMask
  }

  /**
   * Retrieves the power of the DC node in Watts.
   */
  def power: Double =
  {
    if (bufferVoltage != 0)
    {
      //This is a voltage source. Calculate current based on junction current
      return Math.abs(junctionB.currentOut * voltage)
    }
    return current * voltage
  }

  /**
   * Generates a potential difference across the two intersections that go across this node.
   * @param voltage - The target voltage, in Volts
   */
  def generateVoltage(voltage: Double)
  {
    bufferVoltage = voltage
  }

  /**
   * Generates power by adjusting varying the voltage until the target power is reached
   * @param power - The target power, in Watts
   */
  def generatePower(power: Double)
  {
    bufferPower = power
  }

  override def getDebugInfo = List(toString)

  override def toString = "Electric [" + connections.size() + " " + BigDecimal(current).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A " + BigDecimal(voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"

  protected[electric] def calculate()
  {
    voltage = 0
    current = 0

    if (junctionA != null && junctionB != null && junctionA.nodes.size >= 2 && junctionB.nodes.size >= 2)
    {
      // Calculating potential difference across this link.
      voltage = junctionA.voltage - junctionB.voltage

      //If voltage is very small, approximate it to zero
      if (Math.abs(voltage) < 0.0001d)
        voltage = 0

      // Calculating current based on voltage and resistance.
      current = voltage / resistance

      /**
       * Adjust power and balance iet until the voltage creates the desired power.
       */
      if (bufferPower > 0)
      {
        if (current != 0)
        {
          val estimatedResistance = voltage / current
          bufferVoltage = Math.sqrt(estimatedResistance * bufferPower)
        }
        else
        {
          //Generate 1 test volt to determine the resistance of the circuit
          bufferVoltage = 1
        }
      }
    }
  }

  protected[electric] def postUpdate()
  {
    bufferVoltage = 0
    bufferPower = 0
  }

  override protected def newGrid: GridNode[NodeElectricComponent] = new GridElectric

  /**
   * The class used to compare when making connections
   */
  override protected def getCompareClass = getClass
}
