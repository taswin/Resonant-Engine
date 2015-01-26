package resonantengine.prefab.fluid

import net.minecraftforge.fluids.{FluidStack, FluidTankInfo, IFluidTank}

/**
 * Prefab designed to redirecting access to an instance of internally FluidTank instance.
 * This allows any object to be treated as a FluidTank without actually fully implementing its own code.
 *
 * @author Calclavia, DarkCow
 */
trait TFluidTank extends IFluidTank with TTankProvider
{
  override def getFluid: FluidStack = if (getTank != null) getTank.getFluid else null

  override def getFluidAmount: Int = if (getTank != null) getTank.getFluidAmount else 0

  override def getCapacity: Int = if (getTank != null) getTank.getCapacity else 0

  override def getInfo: FluidTankInfo = if (getTank != null) getTank.getInfo else null

  override def fill(resource: FluidStack, doFill: Boolean): Int =
  {
    if (getTank != null)
      return getTank.fill(resource, doFill)
    else
      return 0
  }

  override def drain(maxDrain: Int, doDrain: Boolean): FluidStack =
  {
    if (getTank != null)
      return getTank.drain(maxDrain, doDrain)
    else
      return null
  }

  /** Sets the tank's capacity
    * @param capacity - capacity in milli buckets
    * @return true if the capacity equals the tanks capacity
    */
  def setCapacity(capacity: Int): Boolean =
  {
    if (getTank != null)
    {
      getTank.setCapacity(capacity)
      return getTank.getCapacity == capacity
    }
    return false
  }

  /** Sets the tanks content to null */
  def clearTank(): Boolean =
  {
    if (getTank != null)
    {
      getTank.setFluid(null)
      return getTank.getFluid == null
    }
    return false
  }
}
