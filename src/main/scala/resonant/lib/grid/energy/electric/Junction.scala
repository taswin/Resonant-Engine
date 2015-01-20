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

  var inCurrent = 0d

  var outCurrent = 0d

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
    outCurrent = 0
    inCurrent = 0

    //Loop through every node that is connected to this junction
    nodes.foreach(
      node =>
      {
        //This is required to propagate voltage
        node.calculate()

        if (node.nextVoltage != 0)
        {
          /**
           * Push generated voltages into this node
           */
          if (this == node.junctionA)
          {
            sourceVoltage -= node.nextVoltage / 2
          }
          else if (this == node.junctionB)
          {
            sourceVoltage += node.nextVoltage / 2
          }
        }
        else
        {
          /**
           * Potential difference creates current, which acts to decrease potential difference.
           * Any system forwards to minimal inner energy, and only equipotential systems have minimal energy.
           */
          val delta = node.current * deltaTime

          if (this == node.junctionA)
          {
            voltage -= delta

            //This is junction A of the component. Current should be flowing towards this junction if positive.
            if (node.current > 0)
              inCurrent += node.current
            else
              outCurrent -= node.current
          }
          else if (this == node.junctionB)
          {
            voltage += delta

            //This is junction B of the component. This means current should be leaving this junction if positive.
            if (node.current > 0)
              outCurrent += node.current
            else
              inCurrent -= node.current
          }
        }
      }
    )

    if (sourceVoltage != 0)
      voltage = sourceVoltage

    //TODO: By Kirchoff's Law, current coming in should equal to current going out. Attempt to re-balance that.
  }
}
