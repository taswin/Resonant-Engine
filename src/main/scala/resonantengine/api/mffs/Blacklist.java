package resonantengine.api.mffs;

import net.minecraft.block.Block;

import java.util.HashSet;
import java.util.Set;

public class Blacklist
{
	/**
	 * Adds blocks to this black list if you do not wish them to be interacted by the following:
	 */
	public static final Set<Block> stabilizationBlacklist = new HashSet();
	public static final Set<Block> disintegrationBlacklist = new HashSet();
	public static final Set<Block> mobilizerBlacklist = new HashSet();

}
