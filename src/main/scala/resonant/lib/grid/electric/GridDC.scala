package resonant.lib.grid.electric

import resonant.lib.grid.branch2.GridBranch

/**
 * A direct current electricity grid.
 * @author Calclavia
 */
class GridDC extends GridBranch[DCNode](classOf[DCNode])
{

}
