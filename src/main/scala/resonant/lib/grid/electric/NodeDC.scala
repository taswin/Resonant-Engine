package resonant.lib.grid.electric

import java.util
import java.util.{Set => JSet}

import net.minecraftforge.common.util.ForgeDirection
import resonant.api.tile.INodeProvider
import resonant.lib.debug.DebugInfo
import resonant.lib.grid.node.{NodeGrid, TTileConnector}

/**
 * Represents a direct current component within a circuit.
 *
 * Based on the concept of electricity as being the flow of charge.
 *
 * Charge is taken from negative terminals and pushed to positive terminals. Depending on the change of charge, we can calculate instantaneous current.
 *
 * This is not 100% realistic, but should contain similar mechanics as realistic electricity.
 *
 * TODO: Check why += is not working.
 *
 * @author Calclavia
 */
class NodeDC(parent: INodeProvider) extends NodeGrid[NodeDC](parent) with TTileConnector[NodeDC] with DebugInfo
{
  /**
   * Charges are pushed to positive terminals. Any connections that is NOT
   */
  val positiveTerminals: JSet[ForgeDirection] = new util.HashSet()

  var voltage = 0d
  var resistance = 0d

  override def getDebugInfo = List(toString)

  override def toString = "DC [" + connections.size() + " " + BigDecimal(current).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "A " + BigDecimal(voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"

  /**
   * The class used to compare when making connections
   */
  override protected def getCompareClass = getClass
}
