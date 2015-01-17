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

  var sourceVoltage = 0d

  var incomingVoltage = 0d

  /**
   * The nodes that this junction is connected with.
   */
  var nodes = Set.empty[NodeDC]

  /**
   * The wires that collapsed into this junction
   */
  var wires = Set.empty[NodeDC]

  def update(deltaTime: Double)
  {
    sourceVoltage = 0
    voltage = 0

    //Loop through every node that is connected to this junction
    nodes.foreach(
      node =>
      {
        /**
         * Potential difference creates current, which acts to decrease potential difference.
         * Any system forwards to minimal inner energy, and only equipotential systems have minimal energy.
         */
        val delta = node.current * deltaTime

        if (this == node.junctionA)
          incomingVoltage -= delta
        else if (this == node.junctionB)
          incomingVoltage += delta

        /**
         * Push generated voltages into this node
         */
        if (node.nextVoltage != 0)
        {
          if (this == node.junctionA)
            sourceVoltage -= node.nextVoltage / 2
          if (this == node.junctionB)
            sourceVoltage += node.nextVoltage / 2

          node.nextVoltage = 0
        }
      }
    )

    voltage = sourceVoltage + incomingVoltage
  }
}
