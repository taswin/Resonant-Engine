package resonant.lib.grid.electric

/**
 * A link is the connection across two intersections, with a component in the center.
 * @author Calclavia
 */
class Link(val intersectionA: Intersection, val intersectionB: Intersection)
{
  var resistance = 0d
  var voltage = 0d
  var current = 0d

  def calculate()
  {
    // Calculating potential difference across this link.
    voltage = intersectionA.potential - intersectionB.potential

    // Calculating current based on voltage and resistance.
    current = voltage / resistance
  }
}