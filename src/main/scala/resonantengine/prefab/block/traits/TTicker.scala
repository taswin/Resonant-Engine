package resonantengine.prefab.block.traits

/**
 * An object that can handle ticks.
 * @author Calclavia
 */
trait TTicker
{
  protected var ticks = 0L

  def update()
  {
    if (ticks == 0)
    {
      start()
    }

    if (ticks >= Long.MaxValue)
    {
      ticks = 1
    }

    ticks += 1
  }

  /**
   * Called on the TileEntity's first tick.
   */
  def start()
  {
  }
}
