package resonant.lib.grid.electric

/**
 * An junction is the intersection between multiple nodes.
 *
 * This class is for internal circuit use.
 *
 * @author Calclavia
 */
class Junction
{
  /**
   * The electric potential at this junction.
   */
  var voltage = 0d

  /**
   * The nodes that this junction is connected with.
   */
  var nodes = Set.empty[NodeDC]

  /**
   * The wires that collapsed into this junction
   */
  var wires = Set.empty[NodeDC]
}
