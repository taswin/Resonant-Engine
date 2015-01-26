package resonantengine.test.transform.vector;

import junit.framework.TestCase;
import resonantengine.lib.transform.vector.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Tests Vector3 sorting
 *
 * @author Calclavia
 */
public class Vector3Sorter extends TestCase
{
	public class Vector3DistanceComparator implements Comparator<Vector3>
	{
		final Vector3 center;
		final boolean closest;

		public Vector3DistanceComparator(Vector3 center)
		{
			this(center, true);
		}

		public Vector3DistanceComparator(Vector3 center, boolean closest)
		{
			this.center = center;
			this.closest = closest;
		}

		@Override
		public int compare(Vector3 o1, Vector3 o2)
		{
			double d = o1.distance(center);
			double d2 = o2.distance(center);
			return d > d2 ? 1 : d == d2 ? 0 : -1;
		}
	}

	public void testClosestSorter()
	{
		List<Vector3> list = new ArrayList();
		Vector3 vec_1 = newVector(list, 1, 0, 1);
		Vector3 vec_2 = newVector(list, 2, 0, 2);
		Vector3 vec_3 = newVector(list, 3, 0, 3);
		Vector3 vec_4 = newVector(list, -1, 0, -2);
		Vector3 vec_5 = newVector(list, -4, 0, -4);

		Collections.sort(list, new Vector3DistanceComparator(new Vector3(0, 0, 0)));
		assertEquals(list.get(0), vec_1);
		assertEquals(list.get(1), vec_4);
		assertEquals(list.get(2), vec_2);
		assertEquals(list.get(3), vec_3);
		assertEquals(list.get(4), vec_5);
	}

	private Vector3 newVector(List<Vector3> list, int x, int y, int z)
	{
		Vector3 vec = new Vector3(x, y, z);
		list.add(vec);
		return vec;
	}
}
