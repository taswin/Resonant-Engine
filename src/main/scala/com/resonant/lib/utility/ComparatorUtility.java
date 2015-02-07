package com.resonant.lib.utility;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import nova.core.util.transform.Vector3d;

import java.util.Comparator;

/**
 * Mainly a collection of simple comparators to reduce class count in the package
 * @author Darkguardsman
 */
public class ComparatorUtility {
	public static class YCoordSorter implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			double y = 0;
			double y2 = 0;
			if (arg0 instanceof TileEntity) {
				y = ((TileEntity) arg0).yCoord;
			} else if (arg0 instanceof Entity) {
				y = ((Entity) arg0).posY;
			}

			if (arg1 instanceof TileEntity) {
				y2 = ((TileEntity) arg1).yCoord;
			} else if (arg1 instanceof Entity) {
				y2 = ((Entity) arg1).posY;
			} else if (arg1 instanceof Vector3d) {
				y2 = ((Vector3d) arg1).y();
			}
			return Double.compare(y, y2);
		}
	}

	public static class VectorYCoordSorter implements Comparator<Vector3d> {
		@Override
		public int compare(Vector3d arg0, Vector3d arg1) {
			return Double.compare(arg0.y(), arg1.y());
		}
	}

	public static class VectorXCoordSorter implements Comparator<Vector3d> {
		@Override
		public int compare(Vector3d arg0, Vector3d arg1) {
			return Double.compare(arg0.x(), arg1.x());
		}
	}

	public static class VectorZCoordSorter implements Comparator<Vector3d> {
		@Override
		public int compare(Vector3d arg0, Vector3d arg1) {
			return Double.compare(arg0.z(), arg1.z());
		}
	}

	public static class TileYCoordSorter implements Comparator<TileEntity> {
		@Override
		public int compare(TileEntity arg0, TileEntity arg1) {
			return arg0.yCoord - arg1.yCoord;
		}
	}

	public static class TileXCoordSorter implements Comparator<TileEntity> {
		@Override
		public int compare(TileEntity arg0, TileEntity arg1) {
			return arg0.xCoord - arg1.xCoord;
		}
	}

	public static class TileZCoordSorter implements Comparator<TileEntity> {
		@Override
		public int compare(TileEntity arg0, TileEntity arg1) {
			return arg0.zCoord - arg1.zCoord;
		}
	}
}
