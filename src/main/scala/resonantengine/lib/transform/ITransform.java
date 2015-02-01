package resonantengine.lib.transform;

import nova.core.util.transform.Vector3d;

/**
 * Applied to objects that can transform vectors
 *
 * @Calclavia
 */
public interface ITransform
{
	public Vector3d transform(Vector3d vector);
}
