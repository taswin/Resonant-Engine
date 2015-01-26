package resonantengine.lib.transform;

import resonantengine.lib.transform.vector.Vector3;

/**
 * Applied to objects that can transform vectors
 *
 * @Calclavia
 */
public interface ITransform
{
	public Vector3 transform(Vector3 vector);
}
