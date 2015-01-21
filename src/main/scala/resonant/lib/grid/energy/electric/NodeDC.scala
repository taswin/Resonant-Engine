package resonant.lib.grid.energy.electric

import java.util
import java.util.{Set => JSet}

import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.INodeProvider
import resonant.lib.debug.IDebugInfo
import resonant.lib.grid.core.{GridNode, NodeGrid, TTileConnector}

import scala.beans.BeanProperty
import scala.collection.convert.wrapAll._

/**
 * Represents a direct current component within a circuit.
 *
 * A DC component must be in between two junction nodes in order to function.
 *
 * Flow of current should be positive when current from junction A is flowing to B
 *
 * @author Calclavia
 */
class NodeDC(parent: INodeProvider) extends NodeGrid[NodeDC](parent) with TTileConnector[NodeDC] with IDebugInfo
{
  /**
   * The positive terminals are the directions in which charge can flow out of this DC component.
   */
  val positiveTerminals: JSet[ForgeDirection] = new util.HashSet()

  /**
   * The negative terminals are the directions in which charge can flow into this DC component.
   */
  val negativeTerminals: JSet[ForgeDirection] = new util.HashSet()

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
  protected[electric] var nextVoltage = 0d
  protected[electric] var nextPower = 0d

  /**
   * Junction A is always preferably negative
   */
  protected[electric] var junctionA: Junction = null

  /**
   * Junction B is always preferably positive
   */
  protected[electric] var junctionB: Junction = null

  def positives: JSet[NodeDC] = directionMap.filter(keyVal => positiveTerminals.contains(keyVal._2)).keySet

  def negatives: JSet[NodeDC] = directionMap.filter(keyVal => negativeTerminals.contains(keyVal._2)).keySet

  /**
   * Retrieves the power of the DC node in Watts.
   */
  def power = current * voltage

  /**
   * Generates a potential difference across the two intersections that go across this node.
   * @param voltage - The target voltage, in Volts
   */
  def generateVoltage(voltage: Double)
  {
    nextVoltage = voltage
  }

  /**
   * Generates power by adjusting varying the voltage until the target power is reached
   * @param power - The target power, in Watts
   */
  def generatePower(power: Double)
  {
    nextPower = power
  }

  override def getDebugInfo = List(toString)

  override def toString = "DC [" + connections.size() + " " + BigDecimal(current).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A " + BigDecimal(voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"

  protected[electric] def calculate()
  {
    voltage = 0
    current = 0

    if (junctionA != null && junctionB != null)
    {
      // Calculating potential difference across this link.
      voltage = junctionA.voltage - junctionB.voltage

      //If voltage is very small, approximate it to zero
      if (Math.abs(voltage) < 0.0001d)
        voltage = 0

      if (nextVoltage != 0)
      {
        //This is a voltage source. Calculate current based on junction current
        current = Math.max(Math.max(Math.abs(junctionA.inCurrent), Math.abs(junctionA.outCurrent)), Math.max(Math.abs(junctionB.inCurrent), Math.abs(junctionB.outCurrent))) * Math.signum(voltage)
      }
      else
      {
        // Calculating current based on voltage and resistance.
        current = voltage / resistance
      }

      /**
       * Adjust power and balance iet until the voltage creates the desired power.
       */
      if (nextPower > 0)
      {
        if (current != 0)
        {
          val estimatedResistance = voltage / current
          nextVoltage = Math.sqrt(estimatedResistance * nextPower)
        }
        else
        {
          //Generate 1 test volt to determine the resistance of the circuit
          nextVoltage = 1
        }
      }
    }
  }

  protected[electric] def postUpdate()
  {
    nextVoltage = 0
    nextPower = 0
  }

  override protected def newGrid: GridNode[NodeDC] = new GridDC

  /**
   * The class used to compare when making connections
   */
  override protected def getCompareClass = getClass
}
