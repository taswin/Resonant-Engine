package resonantengine.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Applied to any TileEntity that has a rotation based placement.
 * Can be used to get the facing direction of the machine, and in some
 * case can be used to change the rotation of the machine. Though not
 * all machines support this option
 *
 * @author Calclavia
 */
public interface IRotatable extends IRotation
{
	/** Sets the facing direction, is not supported by all machines */
	public void setDirection(ForgeDirection direction);
}
