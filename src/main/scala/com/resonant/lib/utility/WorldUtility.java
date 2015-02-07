package com.resonant.lib.utility;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import nova.core.util.Direction;
import nova.core.util.transform.Vector3d;
import resonantengine.core.Reference;
import resonantengine.lib.transform.rotation.Quaternion;
import resonantengine.lib.wrapper.BitmaskWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class full of generic World related methods
 * @author DarkGuardsman
 */
public class WorldUtility {
	public static void rotateVectorFromDirection(Vector3d vec, Direction dir) {
		switch (dir) {
			default:
				break;
			case UP:
				vec.transform(new Quaternion(180, Vector3d.east()));
				break;
			case NORTH:
				vec.transform(new Quaternion(90, Vector3d.west()));
				break;
			case SOUTH:
				vec.transform(new Quaternion(90, Vector3d.east()));
				break;
			case WEST:
				vec.transform(new Quaternion(90, Vector3d.north()));
				break;
			case EAST:
				vec.transform(new Quaternion(90, Vector3d.south()));
				break;
		}
	}

	public static int getAngleFromDirection(Direction dir) {
		switch (dir) {
			default:
				break;
			case NORTH:
				return 90;
			case SOUTH:
				return -90;
			case WEST:
				return -180;
			case EAST:
				return 0;
		}

		return 0;
	}

	@SuppressWarnings("incomplete-switch")
	public static Direction invertX(Direction dir) {
		switch (dir) {
			case NORTH:
				return Direction.SOUTH;
			case SOUTH:
				return Direction.NORTH;
		}

		return dir;
	}

	@SuppressWarnings("incomplete-switch")
	public static Direction invertY(Direction dir) {
		switch (dir) {
			case UP:
				return Direction.DOWN;
			case DOWN:
				return Direction.UP;
		}

		return dir;
	}

	@SuppressWarnings("incomplete-switch")
	public static Direction invertZ(Direction dir) {
		switch (dir) {
			case WEST:
				return Direction.EAST;
			case EAST:
				return Direction.WEST;
		}

		return dir;
	}

	/**
	 * Used to find all tileEntities sounding the location you will have to filter for selective
	 * tileEntities
	 * @return an array of up to 6 tileEntities
	 */
	public static TileEntity[] getSurroundingTileEntities(TileEntity ent) {
		return getSurroundingTileEntities(ent.getWorldObj(), ent.xCoord, ent.yCoord, ent.zCoord);
	}

	public static TileEntity[] getSurroundingTileEntities(World world, Vector3d vec) {
		return getSurroundingTileEntities(world, vec.xi(), vec.yi(), vec.zi());
	}

	public static TileEntity[] getSurroundingTileEntities(World world, int x, int y, int z) {
		TileEntity[] list = new TileEntity[6];
		for (Direction direction : Direction.VALID_DIRECTIONS) {
			list[direction.ordinal()] = world.getTileEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
		}
		return list;
	}

	/**
	 * Used to find which of 4 Corners this block is in a group of blocks 0 = not a corner 1-4 = a
	 * corner of some direction
	 */
	public static int corner(TileEntity entity) {
		TileEntity[] en = getSurroundingTileEntities(entity.getWorldObj(), entity.xCoord, entity.yCoord, entity.zCoord);
		TileEntity north = en[Direction.NORTH.ordinal()];
		TileEntity south = en[Direction.SOUTH.ordinal()];
		TileEntity east = en[Direction.EAST.ordinal()];
		TileEntity west = en[Direction.WEST.ordinal()];

		if (west != null && north != null && east == null && south == null) {
			return 3;
		}
		if (north != null && east != null && south == null && west == null) {
			return 4;
		}
		if (east != null && south != null && west == null && north == null) {
			return 1;
		}
		if (south != null && west != null && north == null && east == null) {
			return 2;
		}

		return 0;
	}

	/**
	 * gets all EntityItems in a location using a start and end point
	 */
	public static List<EntityItem> findAllItemsIn(World world, Vector3d start, Vector3d end) {
		return world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(start.x(), start.y(), start.z(), end.x(), end.y(), end.z()));
	}

	public static List<EntityItem> getEntitiesInDirection(World world, Vector3d center, Direction dir) {
		List<EntityItem> list = world.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(center.x() + dir.offsetX, center.y() + dir.offsetY, center.z() + dir.offsetZ, center.x() + dir.offsetX + 1, center.y() + dir.offsetY + 1, center.z() + dir.offsetZ + 1), IEntitySelector.selectAnything);
		return list.size() > 0 ? list : null;
	}

	/**
	 * Gets all EntityItems in an area and sorts them by a list of itemStacks
	 * @param world - world being worked in
	 * @param start - start point
	 * @param end - end point
	 * @param disiredItems - list of item that are being looked for
	 * @return a list of EntityItem that match the itemStacks desired
	 */
	public static List<EntityItem> findSelectItems(World world, Vector3d start, Vector3d end, List<ItemStack> disiredItems) {
		List<EntityItem> entityItems = findAllItemsIn(world, start, end);
		return filterEntityItemsList(entityItems, disiredItems);
	}

	/**
	 * filters an EntityItem List to a List of Items
	 */
	public static List<EntityItem> filterEntityItemsList(List<EntityItem> entityItems, List<ItemStack> disiredItems) {
		List<EntityItem> newItemList = new ArrayList<EntityItem>();
		for (ItemStack itemStack : disiredItems) {
			for (EntityItem entityItem : entityItems) {
				if (entityItem.getEntityItem().isItemEqual(itemStack) && !newItemList.contains(entityItem)) {
					newItemList.add(entityItem);
					break;
				}
			}
		}
		return newItemList;
	}

	/**
	 * filters out EnittyItems from an Entity list
	 */
	public static List<EntityItem> filterOutEntityItems(List<Entity> entities) {
		List<EntityItem> newEntityList = new ArrayList<EntityItem>();

		for (Entity entity : entities) {
			if (entity instanceof EntityItem) {
				newEntityList.add((EntityItem) entity);
			}

		}
		return newEntityList;
	}

	/**
	 * filter a list of itemStack to another list of itemStacks
	 * @param totalItems - full list of items being filtered
	 * @param desiredItems - list the of item that are being filtered too
	 * @return a list of item from the original that are wanted
	 */
	public static List<ItemStack> filterItems(List<ItemStack> totalItems, List<ItemStack> desiredItems) {
		List<ItemStack> newItemList = new ArrayList<ItemStack>();

		for (ItemStack entityItem : totalItems) {
			for (ItemStack itemStack : desiredItems) {
				if (entityItem.getItem() == itemStack.getItem() && entityItem.getItemDamage() == itemStack.getItemDamage() && !newItemList.contains(entityItem)) {
					newItemList.add(entityItem);
					break;
				}
			}
		}
		return newItemList;
	}

	public static void replaceTileEntity(Class<? extends TileEntity> findTile, Class<? extends TileEntity> replaceTile) {
		try {
			Map<String, Class> nameToClassMap = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "70326_a", "nameToClassMap", "a");
			Map<Class, String> classToNameMap = ObfuscationReflectionHelper.getPrivateValue(TileEntity.class, null, "field_" + "70326_b", "classToNameMap", "b");

			String findTileID = classToNameMap.get(findTile);

			if (findTileID != null) {
				nameToClassMap.put(findTileID, replaceTile);
				classToNameMap.put(replaceTile, findTileID);
				classToNameMap.remove(findTile);
				Reference.logger().info("Replaced TileEntity: " + findTile);
			} else {
				Reference.logger().error("Failed to replace TileEntity: " + findTile);
			}
		} catch (Exception e) {
			Reference.logger().error("Failed to replace TileEntity: " + findTile);
			e.printStackTrace();
		}
	}

	/**
	 * Checks based on a bitmap for sides if this side can be rendered.
	 * @return True if so.
	 */
	@Deprecated
	public static boolean isEnabledSide(int sideMap, Direction direction) {
		return BitmaskWrapper.mask(sideMap, direction);
	}

	@Deprecated
	public static int setEnableSide(int sideMap, Direction direction, boolean doEnable) {
		return BitmaskWrapper.mask(sideMap, direction, doEnable);
	}

	public static List<ItemStack> getItemStackFromBlock(World world, int i, int j, int k) {
		Block block = world.getBlock(i, j, k);

		if (block == null) {
			return null;
		}

		if (block.isAir(world, i, j, k)) {
			return null;
		}

		int meta = world.getBlockMetadata(i, j, k);

		ArrayList<ItemStack> dropsList = block.getDrops(world, i, j, k, meta, 0);
		float dropChance = ForgeEventFactory.fireBlockHarvesting(dropsList, world, block, i, j, k, meta, 0, 1.0F, false, DummyPlayer.get(world));

		ArrayList<ItemStack> returnList = new ArrayList<ItemStack>();
		for (ItemStack s : dropsList) {
			if (world.rand.nextFloat() <= dropChance) {
				returnList.add(s);
			}
		}

		return returnList;
	}
}
