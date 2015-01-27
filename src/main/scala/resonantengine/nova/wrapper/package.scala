package resonantengine.nova

import nova.core.util.transform.Operator

/**
 * Wraps NOVA objects and provides them with Scala synthetic sugar coating.
 * @author Calclavia
 */
package object wrapper
{
  implicit class OperatorWrapper[I <: Operator[I, O], O <: I](underlying: Operator[I, O])
  {
    def +(other: I): O = underlying.add(other)

    def +(other: Double): O = underlying.add(other)

    def -(other: I): O = underlying.subtract(other)

    def -(other: Double): O = underlying.subtract(other)

    def *(other: I): O = underlying.multiply(other)

    def *(other: Double): O = underlying.multiply(other)

    def /(other: I): O = underlying.divide(other)

    def /(other: Double): O = underlying.divide(other)

    def unary_+ : O = underlying.asInstanceOf[O]

    def unary_- : O = underlying.inverse()
  }

  /**
   * Temporary Minecraft Bridge
   */
}
