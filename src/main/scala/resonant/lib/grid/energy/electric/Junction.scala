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

  var currentIn = 0d

  var currentOut = 0d

  /**
   * The nodes that this junction is connected with.
   */
  var nodes = Set.empty[NodeElectricComponent]

  /**
   * The wires that collapsed into this junction
   */
  var wires = Set.empty[NodeElectricComponent]

  def update(deltaTime: Double)
  {
    sourceVoltage = 0
    currentOut = 0
    currentIn = 0

    //Loop through every node that is connected to this junction
    nodes.foreach(
      node =>
      {
        //This is required to propagate voltage
        node.calculate()

        if (node.bufferVoltage != 0)
        {
          /**
           * Push generated voltages into this node
           */
          if (this == node.junctionA)
          {
            sourceVoltage -= node.bufferVoltage / 2 * Math.cos(node.frequency * System.currentTimeMillis() / 1000d)
          }
          else if (this == node.junctionB)
          {
            sourceVoltage += node.bufferVoltage / 2 * Math.cos(node.frequency * System.currentTimeMillis() / 1000d)
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
              currentIn += node.current
            else
              currentOut -= node.current
          }
          else if (this == node.junctionB)
          {
            voltage += delta

            //This is junction B of the component. This means current should be leaving this junction if positive.
            if (node.current > 0)
              currentOut += node.current
            else
              currentIn -= node.current
          }
        }
      }
    )

    //TODO: By Kirchoff's Law, current coming in should equal to current going out. Attempt to re-balance that.
    nodes.foreach(
      node =>
      {
        if (node.bufferVoltage != 0)
        {
          if (this == node.junctionA)
          {
            currentIn = currentOut
          }
          else if (this == node.junctionB)
          {
            currentOut = currentIn
          }
        }
      }
    )

    if (sourceVoltage != 0)
      voltage = sourceVoltage
  }

  /**
   * Gets the power loss in this junction due to resistance
   */
  def powerLoss = currentIn * currentIn * resistance

  /**
   * The total resistance of this junction due to wires
   */
  def resistance = wires.map(_.resistance).foldLeft(0d)(_ + _)
}
