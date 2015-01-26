/**
 *
 */
package resonantengine.lib.utility;

import net.minecraft.item.ItemStack;
import resonantengine.lib.utility.nbt.NBTUtility;
import resonantengine.lib.transform.vector.VectorWorld;

/**
 * @author Calclavia
 */
public class LinkUtility
{
	public static boolean hasLink(ItemStack itemStack)
	{
		return getLink(itemStack) != null;
	}

	public static VectorWorld getLink(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null || !itemStack.getTagCompound().hasKey("link"))
		{
			return null;
		}

		return new VectorWorld(itemStack.getTagCompound().getCompoundTag("link"));
	}

	public static void setLink(ItemStack itemStack, VectorWorld vec)
	{
		NBTUtility.getNBTTagCompound(itemStack).setTag("link", vec.toNBT());
	}

	public static void clearLink(ItemStack itemStack)
	{
		itemStack.getTagCompound().removeTag("link");
	}

	public static void setSide(ItemStack itemStack, byte side)
	{
		NBTUtility.getNBTTagCompound(itemStack).setByte("linkSide", side);
	}

	public static byte getSide(ItemStack itemStack)
	{
		return NBTUtility.getNBTTagCompound(itemStack).getByte("linkSide");
	}
}
