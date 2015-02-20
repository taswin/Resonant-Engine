package com.resonant.wrapper.lib.utility;

import nova.core.block.Block;
import nova.core.fluid.Fluid;
import nova.core.fluid.FluidBlock;
import nova.core.fluid.Tank;
import nova.core.fluid.TankProvider;
import nova.core.util.Direction;
import nova.core.util.collection.Pair;
import nova.core.util.transform.Vector3d;
import nova.core.util.transform.Vector3i;
import nova.core.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Fluid interaction helper methods.
 *
 * @author Calclavia
 */
public class FluidUtility {

	public static int getFluidAmountFromBlock(World world, Vector3i vector) {
		Optional<Fluid> fluid = getFluidFromBlock(world, vector);
		return fluid.isPresent() ? fluid.get().amount() : 0;
	}

	public static Optional<Fluid> getFluidFromBlock(World world, Vector3i pos) {
		Optional<Block> block = world.getBlock(pos);

		if (block.isPresent() && block.get() instanceof FluidBlock) {
			return ((FluidBlock) block.get()).getFluid();
		}

		return Optional.empty();
	}

	public static Optional<Tank> getTank(World world, Vector3d pos, Direction from) {
		Optional<Block> block = world.getBlock(pos);

		if (block.isPresent() && block.get() instanceof TankProvider) {
			return ((TankProvider) block.get()).getTank(from);
		}

		return Optional.empty();
	}

	public static double getFilledPercentage(Tank... tanks) {
		int amount = 0;
		int capacity = 0;

		for (Tank tank : tanks) {
			amount += tank.getFluidAmount();
			capacity += tank.getFluidCapacity();
		}

		if (capacity > 0) {
			return (double) amount / (double) capacity;
		}

		return 0;
	}

	public static double getAveragePercentageFilledForSides(Class classMask, double defaultFill, World world, Vector3i pos, Direction... sides) {
		double fullness = defaultFill;
		int count = 1;

		for (Direction side : sides) {
			Optional<Block> block = world.getBlock(pos);

			if (block.isPresent() && (classMask == null || classMask.isAssignableFrom(block.get().getClass()))) {
				FluidTankInfo[] info = getTank(world, pos.clone().add(side), side);

				if (info.length > 0) {
					fullness += getFilledPercentage(info);
					count++;
				}
			}
		}

		return Math.max(0, Math.min(1, fullness / count));
	}

	/**
	 * Gets the block's fluid if it has one
	 *
	 * @param world - world we are working in
	 * @param vector - 3D location in world
	 * @return @Fluid that the block is
	 */
	public static Fluid getFluidFromBlock(World world, Vector3d vector) {
		return FluidUtility.getFluidFromBlockID(vector.getBlock(world));
	}

	/**
	 * Gets a fluid from blockID
	 */
	public static Fluid getFluidFromBlockID(Block block) {
		if (block instanceof FluidBlock) {
			return ((FluidBlock) block).getFluid();
		} else if (block == Blocks.water || block == Blocks.flowing_water) {
			return FluidRegistry.WATER;
		} else if (block == Blocks.lava || block == Blocks.flowing_lava) {
			return FluidRegistry.LAVA;
		}

		return null;
	}

	public static Fluid getStack(Fluid stack, int amount) {
		if (stack != null) {
			Fluid newStack = stack.copy();
			newStack.amount = amount;
			return newStack;
		}
		return null;
	}

	public static boolean matchExact(Fluid stack, Fluid stack2) {
		if (stack == null && stack2 == null) {
			return true;
		} else if (stack != null && stack.isFluidEqual(stack2)) {
			return stack.amount == stack2.amount;
		}
		return false;
	}

	/**
	 * Drains a block of fluid
	 *
	 * @param doDrain - do the action
	 * @return Fluid drained from the block
	 * @Note sets the block with a client update only. Doesn't tick the block allowing for better
	 * placement of fluid that can flow infinitely
	 */
	public static Fluid drainBlock(World world, Vector3d position, boolean doDrain) {
		return drainBlock(world, position, doDrain, 3);
	}

	/**
	 * Drains a block of fluid
	 *
	 * @param doDrain - do the action
	 * @param update - block update flag to use
	 * @return Fluid drained from the block
	 */
	public static Fluid drainBlock(World world, Vector3d position, boolean doDrain, int update) {
		if (world == null || position == null) {
			return null;
		}

		Block block = position.getBlock(world);
		int meta = position.getBlockMetadata(world);

		if (block != null) {
			if (block instanceof FluidBlock && ((FluidBlock) block).canDrain(world, position.xi(), position.yi(), position.zi())) {
				return ((FluidBlock) block).drain(world, position.xi(), position.yi(), position.zi(), doDrain);
			} else if ((block == Blocks.water || block == Blocks.flowing_water) && position.getBlockMetadata(world) == 0) {
				if (doDrain) {
					Vector3d vec = position.clone().add(Direction.UP);

					if (vec.getBlock(world) == Blocks.water) {
						vec.setBlock(world, Blocks.air, 0, update);
						position.setBlock(world, block, meta);
					} else {
						position.setBlock(world, Blocks.air, 0, update);
					}
				}
				return new Fluid(FluidRegistry.WATER, Fluid.bucketVolume);
			} else if ((block == Blocks.lava || block == Blocks.flowing_lava) && position.getBlockMetadata(world) == 0) {
				if (doDrain) {
					position.setBlock(world, Blocks.air, 0, update);
				}
				return new Fluid(FluidRegistry.LAVA, Fluid.bucketVolume);
			}
		}
		return null;
	}

	/**
	 * Checks to see if a non-fluid block is able to be filled with fluid
	 */
	public static boolean isFillableBlock(World world, Vector3d node) {
		if (world == null || node == null) {
			return false;
		}

		Block block = node.getBlock(world);
		int meta = node.getBlockMetadata(world);

		if (drainBlock(world, node, false) != null) {
			return false;
		} else if (block.isAir(world, node.xi(), node.yi(), node.zi())) {
			return true;
		} else if (!(block instanceof FluidBlock || block instanceof BlockLiquid) && block.isReplaceable(world, node.xi(), node.yi(), node.zi()) || replacableBlockMeta.contains(new Pair(block, meta)) || replacableBlocks.contains(block)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks to see if a fluid related block is able to be filled
	 */
	public static boolean isFillableFluid(World world, Vector3d node) {
		if (world == null || node == null) {
			return false;
		}

		Block block = node.getBlock(world);
		int meta = node.getBlockMetadata(world);

		// TODO when added change this to call canFill and fill
		if (drainBlock(world, node, false) != null) {
			return false;
		} else if (block instanceof FluidBlock || block instanceof BlockLiquid) {
			return meta != 0;
		}
		return false;
	}

	/**
	 * Helper method to fill a location with a fluid
	 * <p/>
	 * Note: This does not update the block to prevent the liquid from flowing
	 *
	 * @return
	 */
	public static int fillBlock(World world, Vector3d node, Fluid stack, boolean doFill) {
		if ((isFillableBlock(world, node) || isFillableFluid(world, node)) && stack != null && stack.amount >= Fluid.bucketVolume) {
			if (doFill) {
				Block block = node.getBlock(world);
				int meta = node.getBlockMetadata(world);

				Vector3d vec = node.clone().add(Direction.UP);

				if (block != null) {
					if (block == Blocks.water && vec.getBlock(world).isAir(world, node.xi(), node.yi(), node.zi())) {
						vec.setBlock(world, block, meta);
					} else if (replacableBlocks.contains(block) && !nonBlockDropList.contains(block)) {
						block.dropBlockAsItem(world, node.xi(), node.yi(), node.zi(), meta, 1);
						block.breakBlock(world, node.xi(), node.yi(), node.zi(), block, meta);
					}
				}

				node.setBlock(world, stack.getFluid().getBlock());
			}
			return Fluid.bucketVolume;
		}
		return 0;
	}

	/**
	 * Fills all instances of IFluidHandler surrounding the origin
	 *
	 * @param stack - Fluid that will be filled into the tanks
	 * @param doFill - Actually perform the action or simulate action
	 * @param ignore - Directions to ignore
	 * @return amount of fluid that was used from the stack
	 */
	public static int fillTanksAllSides(World world, Vector3d origin, Fluid stack, boolean doFill, Direction... ignore) {
		int filled = 0;
		Fluid fillStack = stack != null ? stack.copy() : null;
		for (Direction direction : Direction.VALID_DIRECTIONS) {
			if (fillStack == null || fillStack.amount <= 0) {
				return filled;
			}
			if (ignore != null) {
				for (int i = 0; i < ignore.length; i++) {
					if (direction == ignore[i]) {
						continue;
					}
				}
			}
			filled += fillTankSide(world, origin, stack, doFill, direction);
			fillStack = getStack(stack, stack.amount - filled);

		}
		return filled;
	}

	/**
	 * Fills an instance of IFluidHandler in the given direction
	 *
	 * @param stack - Fluid to fill the tank will
	 * @param doFill - Actually perform the action or simulate action
	 * @param direction - direction to fill in from the origin
	 * @return amount of fluid that was used from the stack
	 */
	public static int fillTankSide(World world, Vector3d origin, Fluid stack, boolean doFill, Direction direction) {
		TileEntity entity = origin.clone().add(direction).getTileEntity(world);
		if (entity instanceof IFluidHandler && ((IFluidHandler) entity).canFill(direction.getOpposite(), stack.getFluid())) {
			return ((IFluidHandler) entity).fill(direction.getOpposite(), stack, doFill);
		}
		return 0;
	}

	public static int fillAllTanks(List<IFluidTank> tanks, Fluid resource, boolean doFill) {
		int totalFilled = 0;
		Fluid fill = resource.copy();

		for (IFluidTank tank : tanks) {
			if (fill.amount > 0) {
				int filled = tank.fill(fill, doFill);
				totalFilled += filled;
				fill.amount -= filled;
			} else {
				break;
			}
		}

		return totalFilled;
	}

	public static Fluid drainAllTanks(List<IFluidTank> tanks, int amount, boolean doDrain) {
		Fluid drain = null;

		for (IFluidTank tank : tanks) {
			if (drain != null && drain.amount >= amount) {
				break;
			}

			Fluid drained = tank.drain(amount, false);

			if (drained != null) {
				if (drain == null) {
					drain = drained;
					tank.drain(amount, doDrain);
				} else if (drain.equals(drained)) {
					drain.amount += drained.amount;
					tank.drain(amount, doDrain);
				}
			}
		}

		return drain;
	}

	/**
	 * Does all the work needed to fill or drain an item of fluid when a player clicks on the block.
	 */
	public static boolean playerActivatedFluidItem(World world, int x, int y, int z, EntityPlayer entityplayer, int side) {
		// TODO Add double click support similar to the crates in assembly line
		ItemStack current = entityplayer.inventory.getCurrentItem();

		if (current != null && world.getTileEntity(x, y, z) instanceof IFluidHandler) {
			Fluid fluid = FluidContainerRegistry.getFluidForFilledItem(current);
			IFluidHandler tank = (IFluidHandler) world.getTileEntity(x, y, z);

			if (fluid != null) {
				if (tank.fill(Direction.fromOrdinal(side), fluid.copy(), false) == fluid.amount) {
					tank.fill(Direction.fromOrdinal(side), fluid.copy(), true);
					if (!entityplayer.capabilities.isCreativeMode) {
						InventoryUtility.consumeHeldItem(entityplayer);
					}
					return true;
				}
			} else {
				Fluid available = tank.drain(Direction.fromOrdinal(side), Integer.MAX_VALUE, false);

				if (available != null) {
					ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);

					fluid = FluidContainerRegistry.getFluidForFilledItem(filled);

					if (fluid != null) {
						if (!entityplayer.capabilities.isCreativeMode) {
							if (!entityplayer.inventory.addItemStackToInventory(filled)) {
								return false;
							} else {
								InventoryUtility.dropItemStack(new VectorWorld(entityplayer), filled);
							}
						}
						tank.drain(Direction.UNKNOWN, fluid.amount, true);
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean playerActivatedFluidItem(List<IFluidTank> tanks, EntityPlayer entityplayer, int side) {
		ItemStack current = entityplayer.inventory.getCurrentItem();

		if (current != null) {
			Fluid resource = FluidContainerRegistry.getFluidForFilledItem(current);

			if (resource != null) {
				if (fillAllTanks(tanks, resource, false) >= resource.amount) {
					fillAllTanks(tanks, resource, true);

					if (!entityplayer.capabilities.isCreativeMode) {
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, AutoCraftingManager.consumeItem(current, 1));
					}
					return true;
				}
			} else {
				Fluid available = drainAllTanks(tanks, Integer.MAX_VALUE, false);

				if (available != null) {
					ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);

					resource = FluidContainerRegistry.getFluidForFilledItem(filled);

					if (resource != null) {
						if (!entityplayer.capabilities.isCreativeMode) {
							if (current.stackSize > 1) {
								if (!entityplayer.inventory.addItemStackToInventory(filled)) {
									return false;
								} else {
									entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, AutoCraftingManager.consumeItem(current, 1));
								}
							} else {
								entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, AutoCraftingManager.consumeItem(current, 1));
								entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, filled);
							}
						}

						drainAllTanks(tanks, resource.amount, true);
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Drains an item of fluid and fills the tank with what was drained
	 *
	 * @param stack - should it consume the item. Used mainly for creative mode players. This
	 * does effect the return of the method
	 * @return Item stack that would be returned if the item was drain of its fluid. Water bucket ->
	 * empty bucket
	 */
	public static ItemStack drainItem(ItemStack stack, IFluidHandler tank, Direction side) {
		if (stack != null && tank != null) {
			Fluid liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
			if (liquid != null) {
				if (tank.fill(side, liquid, true) > 0) {
					return stack.getItem().getContainerItem(stack);
				}
			}
		}
		return stack;
	}

	/**
	 * Fills an item with fluid from the tank
	 *
	 * @param stack - should it consume the item. Used mainly for creative mode players. This
	 * does effect the return of the method
	 * @return Item stack that would be returned if the item was filled with fluid. empty bucket ->
	 * water bucket
	 */
	public static ItemStack fillItem(ItemStack stack, IFluidHandler tank, Direction side) {
		if (stack != null && tank != null) {
			Fluid liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
			Fluid drainStack = tank.drain(side, Integer.MAX_VALUE, false);
			if (liquid == null && drainStack != null) {
				ItemStack liquidItem = FluidContainerRegistry.fillFluidContainer(drainStack, stack);
				if (tank.drain(side, FluidContainerRegistry.getFluidForFilledItem(liquidItem), true) != null) {
					return liquidItem;
				}
			}
		}
		return stack;
	}

	/**
	 * Builds a list of Fluids from FluidTankInfo general taken from an instanceof
	 * IFluidHandler
	 */
	public static List<Fluid> getFluidList(FluidTankInfo... fluidTankInfos) {
		List<Fluid> stackList = new ArrayList<Fluid>();
		HashMap<Fluid, Integer> map = new HashMap<Fluid, Integer>();
		if (fluidTankInfos != null) {
			for (int i = 0; i < fluidTankInfos.length; i++) {
				FluidTankInfo info = fluidTankInfos[i];
				if (info != null && info.fluid != null) {
					Fluid stack = info.fluid;
					if (map.containsKey(FluidUtility.getStack(stack, 0))) {
						map.put(FluidUtility.getStack(stack, 0), map.get(FluidUtility.getStack(stack, 0)) + stack.amount);
					} else {
						map.put(FluidUtility.getStack(stack, 0), stack.amount);
					}
				}
			}
			Iterator<Entry<Fluid, Integer>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Fluid, Integer> entry = it.next();
				stackList.add(FluidUtility.getStack(entry.getKey(), entry.getValue()));
			}
		}
		return stackList;

	}
}
