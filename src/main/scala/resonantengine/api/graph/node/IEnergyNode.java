package resonantengine.api.graph.node;

import nova.core.util.Direction;
/// Calclavia, Do not deprecate this class as its not just for usage by the grids. From, DarkGuardsman

/**  Simple interface that allows adding and removing energy from a node.
 *
 * @author Darkguardsman */
public interface IEnergyNode extends INode
{
    /** Adds energy to the node returns energy added */
	public double addEnergy(Direction from, double wattage, boolean doAdd);

    /** Removes energy from the node returns energy removed */
	public double removeEnergy(Direction from, double wattage, boolean doRemove);

	/** Current energy stored in UE joules */
	public double getEnergy(Direction from);

    /** Max limit on energy stored */
	public double getEnergyCapacity(Direction from);
}
