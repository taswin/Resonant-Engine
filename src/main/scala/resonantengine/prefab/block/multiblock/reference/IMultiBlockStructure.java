package resonantengine.prefab.block.multiblock.reference;

import net.minecraft.world.World;
import resonantengine.lib.transform.vector.Vector3;

public interface IMultiBlockStructure<W extends IMultiBlockStructure> extends IMultiBlock
{
	public World getWorld();

	public void onMultiBlockChanged();

	public Vector3 getPosition();

	public MultiBlockHandler<W> getMultiBlock();
}
