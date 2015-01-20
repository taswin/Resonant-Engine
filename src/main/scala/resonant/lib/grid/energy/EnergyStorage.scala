package resonant.lib.grid.energy

import net.minecraft.nbt.NBTTagCompound

/**
 * Can be used internally for IEnergyInterface blocks. This is optional.
 *
 * @author Calclavia
 */
class EnergyStorage
{
  protected var energy: Double = .0
  protected var capacity: Double = .0
  protected var maxReceive: Double = .0
  protected var maxExtract: Double = .0
  /**
   * A cache of the last energy stored through extract and receive.
   */
  protected var lastEnergy: Double = .0

  def this(capacity: Double, maxReceive: Double, maxExtract: Double) =
  {
    this()
    this.capacity = capacity
    this.maxReceive = maxReceive
    this.maxExtract = maxExtract
  }

  def this(capacity: Double) = this(capacity, capacity, capacity)

  def this(capacity: Double, maxTransfer: Double) = this(capacity, maxTransfer, maxTransfer)

  def readFromNBT(nbt: NBTTagCompound): EnergyStorage =
  {
    this.energy = nbt.getDouble("energy")
    return this
  }

  def writeToNBT(nbt: NBTTagCompound): NBTTagCompound =
  {
    nbt.setDouble("energy", this.getEnergy)
    return nbt
  }

  def setCapacity(capacity: Double): EnergyStorage =
  {
    this.capacity = capacity
    if (getEnergy > capacity)
    {
      energy = capacity
    }
    return this
  }

  def setMaxTransfer(maxTransfer: Double): EnergyStorage =
  {
    setMaxReceive(maxTransfer)
    setMaxExtract(maxTransfer)

    return this
  }

  def setMaxReceive(maxReceive: Double)
  {
    this.maxReceive = maxReceive
  }

  def setMaxExtract(maxExtract: Double)
  {
    this.maxExtract = maxExtract
  }

  /**
   * This function is included to allow the containing tile to directly and efficiently modify the
   * energy contained in the EnergyStorage. Do not rely on this
   * externally, as not all IEnergyHandlers are guaranteed to have it.
   *
   * @param energy
   */
  def modifyEnergyStored(energy: Double)
  {
    this.setEnergy(this.getEmptySpace + energy)
    if (this.getEnergy > this.getEnergyCapacity)
    {
      this.setEnergy(this.getEnergyCapacity)
    }
    else if (this.getEnergy < 0)
    {
      this.setEnergy(0)
    }
  }

  /**
   * Returns the amount of energy this storage can further store.
   */
  def getEmptySpace: Double = this.getEnergyCapacity - this.getEnergy

  def receiveEnergy: Double = this.receiveEnergy(true)

  def receiveEnergy(doReceive: Boolean): Double = this.receiveEnergy(this.getMaxReceive, doReceive)

  def extractEnergy: Double = this.extractEnergy(true)

  def extractEnergy(doExtract: Boolean): Double =
  {
    return this.extractEnergy(this.getMaxExtract, doExtract)
  }

  def checkReceive: Boolean = this.checkReceive(this.getMaxReceive)

  def getMaxReceive: Double = maxReceive

  def checkReceive(receive: Double): Boolean = this.receiveEnergy(receive, false) >= receive

  def receiveEnergy(receive: Double, doReceive: Boolean): Double =
  {
    val energyReceived: Double = Math.min(this.getEnergyCapacity - this.getEnergy, Math.min(this.getMaxReceive, receive))
    if (doReceive)
    {
      this.lastEnergy = this.getEnergy
      this.setEnergy(this.getEnergy + energyReceived)
    }
    return energyReceived
  }

  /**
   * This function is included to allow for server -> client sync. Do not call this externally to
   * the containing Tile Entity, as not all IEnergyHandlers are
   * guaranteed to have it.
   *
   * @param energy
   */
  def setEnergy(energy: Double)
  {
    this.energy = energy
    if (this.getEnergy > this.getEnergyCapacity)
    {
      this.energy = this.getEnergyCapacity
    }
    else if (this.getEnergy < 0)
    {
      this.energy = 0
    }
  }

  def getEnergy: Double = this.energy

  def getEnergyCapacity: Double = this.capacity

  def checkExtract: Boolean = this.checkExtract(this.getMaxExtract)

  def getMaxExtract: Double = maxExtract

  def checkExtract(extract: Double): Boolean = this.extractEnergy(extract, false) >= extract

  def extractEnergy(extract: Double, doExtract: Boolean): Double =
  {
    val energyExtracted: Double = Math.min(this.getEnergy, Math.min(this.getMaxExtract, extract))
    if (doExtract)
    {
      this.lastEnergy = this.getEnergy
      this.setEnergy(this.getEnergy - energyExtracted)
    }
    return energyExtracted
  }

  def isFull: Boolean = this.getEnergy >= this.getEnergyCapacity

  def isEmpty: Boolean = this.getEnergy == 0

  /**
   * @return True if the last energy state and the current one are either in an
   *         "empty or not empty" change state.
   */
  def didEnergyStateChange: Boolean =
  {
    return (this.getLastEnergy == 0 && this.getEnergy > 0) || (this.getLastEnergy > 0 && this.getEnergy == 0)
  }

  def getLastEnergy: Double = this.lastEnergy

  override def toString: String =
  {
    return this.getClass.getSimpleName + "[" + this.getEnergy + "/" + this.getEnergyCapacity + "]"
  }

  def +=(d: Double)
  {
    receiveEnergy(d, true)
  }

  def -=(d: Double)
  {
    extractEnergy(d, true)
  }
}