package resonantengine.api;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Simple interface to apply a save and load method to any object
 *
 * @author Darkguardsman
 */
public interface ISave
{
	/**
	 * Saves the object to NBT
	 */
	public void save(NBTTagCompound nbt);

	/**
	 * Load the object from NBT
	 */
	public void load(NBTTagCompound nbt);
}
