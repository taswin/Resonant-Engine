package resonantengine.api.mffs.modules;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import resonantengine.api.mffs.machine.IFieldMatrix;
import resonantengine.api.mffs.machine.IProjector;
import resonantengine.lib.transform.vector.Vector3;

import java.util.Set;

/**
 * A module for any matrix based machines.
 */
public interface IModule extends IFortronCost
{
	/**
	 * Called before the projector projects a field.
	 *
	 * @param projector
	 * @return True to stop projecting.
	 */
	boolean onProject(IProjector projector, Set<Vector3> field);

	boolean onDestroy(IProjector projector, Set<Vector3> field);

	/**
	 * Called right before the projector creates a force field block.
	 *
	 * @return 0 - Do nothing; 1 - Skip this block and continue; 2 - Cancel rest of projection;
	 */
	public int onProject(IProjector projector, Vector3 position);

	/**
	 * Called when an entity collides with a force field block.
	 *
	 * @return False to stop the default process of entity collision.
	 */
	public boolean onCollideWithForceField(World world, int x, int y, int z, Entity entity, ItemStack moduleStack);

	/**
	 * Called in this module when it is being calculated by the projector. Called BEFORE
	 * transformation is applied to the field.
	 *
	 * @return False if to prevent this position from being added to the projection que.
	 */
	public void onPreCalculate(IFieldMatrix projector, Set<Vector3> calculatedField);

	/**
	 * Called in this module when after being calculated by the projector.
	 *
	 * @return False if to prevent this position from being added to the projection que.
	 */
	public void onPostCalculate(IFieldMatrix projector, Set<Vector3> fieldDefinition);

	/**
	 * @param moduleStack
	 * @return Does this module require ticking from the force field projector?
	 */
	public boolean requireTicks(ItemStack moduleStack);

}
