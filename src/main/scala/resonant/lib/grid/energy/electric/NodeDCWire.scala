package resonant.lib.grid.energy.electric

import resonant.api.tile.INodeProvider

/**
 * Wires are nodes in the grid that will not have different terminals, but instead can connect omni-directionally.
 * Wires will be treated as junctions and collapsed.
 * @author Calclavia
 */
class NodeDCWire(parent: INodeProvider) extends NodeDC(parent)
{

  override def toString: String =
  {
    if (junctionA != null)
      "DC [" + connections.size() + " Total: " + BigDecimal(junctionA.voltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V Source: " + BigDecimal(junctionA.sourceVoltage).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "V]"
    else
      "DC [" + connections.size() + " Null Junction]"
  }

}
