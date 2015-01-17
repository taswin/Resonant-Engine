package resonant.lib.grid.energy.electric

import resonant.api.tile.INodeProvider

/**
 * Wires are nodes in the grid that will not have different terminals, but instead can connect omni-directionally.
 * @author Calclavia
 */
class NodeDCWire(parent: INodeProvider) extends NodeDC(parent)
{

}
