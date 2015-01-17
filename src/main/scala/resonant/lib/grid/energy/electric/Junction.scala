package resonant.lib.grid.energy.electric

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

  var nextVoltage = 0d

  /**
   * The nodes that this junction is connected with.
   */
  var nodes = Set.empty[NodeDC]

  /**
   * The wires that collapsed into this junction
   */
  var wires = Set.empty[NodeDC]

  def preUpdate()
  {
  }

  def update(deltaTime: Double)
  {
    nextVoltage = voltage

    nodes.foreach(
      node =>
      {
        node.update(deltaTime)
        /**
         * Potential difference creates current, which acts to decrease potential difference.
         * Any system forwards to minimal inner energy, and only equipotential systems have minimal energy.
         */
        val delta = node.current * deltaTime

        if (this == node.junctionA)
          nextVoltage = nextVoltage - Math.signum(node.potentialDifference) * delta
        else if (this == node.junctionB)
          nextVoltage = nextVoltage + Math.signum(node.potentialDifference) * delta
      }
    )

    voltage = nextVoltage
  }
}
