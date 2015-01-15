package resonant.lib.grid.electric

/**
 * An intersection is the junction between multiple nodes.
 * An intersection may be connected to multiple nodes.
 * @author Calclavia
 */
class Intersection
{
  var potential = 0d
  var links = Set.empty[Link]

  //The DCNodes this intersection is intersecting
  var between = Set.empty[NodeDC]
}
