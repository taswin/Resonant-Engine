package resonantengine.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Applied to any object that has a facing direction.
 *
 * @author Calclavia
 */
public interface IRotatable
{
	/**
	 * Sets the facing direction, is not supported by all machines
	 */
	public void setDirection(ForgeDirection direction);

	/**
	 * Gets the facing direction of the TileEntity
	 *
	 * @return Front of the tile
	 */
	public ForgeDirection getDirection();
}
