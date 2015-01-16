package resonant.lib.grid.electric

import java.util
import java.util.{Set => JSet}

import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.INodeProvider
import resonant.lib.debug.DebugInfo
import resonant.lib.grid.GridNode
import resonant.lib.grid.node.{NodeGrid, TTileConnector}

import scala.beans.BeanProperty
import scala.collection.convert.wrapAll._

/**
 * Represents a direct current component within a circuit.
 *
 * A DC component must be in between two junction nodes in order to function.
 *
 * @author Calclavia
 */
class NodeDC(parent: INodeProvider) extends NodeGrid[NodeDC](parent) with TTileConnector[NodeDC] with DebugInfo
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
  var resistance = 0.1d

  protected[electric] var junctionA: Junction = null
  protected[electric] var junctionB: Junction = null

  def positives: JSet[NodeDC] = directionMap.filter(keyVal => positiveTerminals.contains(keyVal._2)).keySet

  def negatives: JSet[NodeDC] = directionMap.filter(keyVal => negativeTerminals.contains(keyVal._2)).keySet

  /**
   * Retrieves the power of the DC node in Watts.
   */
  def power = current * voltage

  /**
   * Generates a potential difference across the two intersections that go across this node.
   */
  def generateVoltage(voltage: Double)
  {
    if (junctionA != null && junctionB != null)
    {
      junctionA.voltage += voltage / 2
      junctionB.voltage -= voltage / 2
    }
  }

  override def getDebugInfo = List(toString)

  //  override def toString = "DC [" + connections.size() + " " + BigDecimal(current).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A " + BigDecimal(voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"
  override def toString = "DC [" + connections.size() + " " + current + "A " + voltage + "V]"

  protected[electric] def calculate()
  {
    voltage = 0
    current = 0

    if (junctionA != null && junctionB != null)
    {
      // Calculating potential difference across this link.
      voltage = junctionA.voltage - junctionB.voltage

      // Calculating current based on voltage and resistance.
      current = voltage / resistance
    }
  }

  override protected def newGrid: GridNode[NodeDC] = new GridDC

  /**
   * The class used to compare when making connections
   */
  override protected def getCompareClass = getClass
}
