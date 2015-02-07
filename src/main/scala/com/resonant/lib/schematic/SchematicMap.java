package com.resonant.lib.schematic;

import com.resonant.core.api.misc.ISave;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import nova.core.util.Direction;
import nova.core.util.collection.Pair;
import nova.core.util.transform.Vector3d;
import resonantengine.lib.transform.vector.VectorWorld;
import resonantengine.lib.utility.nbt.NBTUtility;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * File that represents all the data loaded from a schematic data file
 * @author DarkGuardsman
 */
public class SchematicMap extends Schematic implements ISave {
	//TODO save the schematics using block names, include a reference sheet to match block names to IDs instead of saving each block as a string

	public static final String BLOCK_LIST_SAVE_NAME = "BlockList";
	public static final String BLOCK_REF_SAVE_NAME = "BlockRef";
	public static final String BLOCK_MAP_SAVE_NAME = "BlockMap";

	private static final LinkedHashMap<String, Block> BLOCK_SAVE_MAP = new LinkedHashMap<String, Block>();
	private static final LinkedHashMap<Block, String> BLOCK_SAVE_MAP_REV = new LinkedHashMap<Block, String>();
	public Vector3d schematicSize;
	public Vector3d schematicCenter;
	public LinkedHashMap<Vector3d, Pair<Block, Integer>> block_map = new LinkedHashMap<Vector3d, Pair<Block, Integer>>();
	public boolean init = false;
	protected String name;

	/**
	 * Manually saves a block by a set name, overrides vanilla block name saving
	 */
	public static void registerSaveBlock(String name, Block block) {
		BLOCK_SAVE_MAP.put(name, block);
		BLOCK_SAVE_MAP_REV.put(block, name);
	}

	public void init() {
		if (this.schematicSize != null) {
			this.init = true;
			this.schematicCenter = new Vector3d();
			this.schematicCenter.x(this.schematicSize.x() / 2);
			// this.schematicCenter.y = this.schematicSize.y() / 2;
			this.schematicCenter.z(this.schematicSize.z() / 2);
		}
	}

	public void build(VectorWorld spot, boolean doWorldCheck) {
		if (this.block_map != null) {
			HashMap<Vector3d, ItemStack> blocksToPlace = new HashMap<Vector3d, ItemStack>();
			this.getBlocksToPlace(spot, blocksToPlace, doWorldCheck, doWorldCheck);
			for (Entry<Vector3d, ItemStack> entry : blocksToPlace.entrySet()) {
				entry.getKey().setBlock(spot.world(), Block.getBlockFromItem(entry.getValue().getItem()), entry.getValue().getItemDamage());
			}
		}
	}

	/**
	 * Gets all blocks needed to build the schematic at the given location
	 * @param spot - center point of the schematic
	 * @param blockMap - map of blocks from the schematic file
	 * @param checkWorld - check if blocks are already placed
	 * @param checkIfWorldIsLoaded - check if the area is loaded, make sure this is true if
	 * checkWorld boolean is true as it effects the results if the chunk is unloaded. Setting this
	 * true will not load the area and is designed to prevent wasting blocks when generating
	 * buildings using actual blocks
	 */
	public void getBlocksToPlace(VectorWorld spot, HashMap<Vector3d, ItemStack> blockMap, boolean checkWorld, boolean checkIfWorldIsLoaded) {
		if (this.block_map != null) {
			for (Entry<Vector3d, Pair<Block, Integer>> entry : this.block_map.entrySet()) {
				Block block = entry.getValue().left();
				int meta = entry.getValue().right();

				if (block == null || block != Blocks.sponge) {
					if (meta > 15) {
						meta = 15;
					} else if (meta < 0) {
						meta = 0;
					}
					Vector3d setPos = spot.clone().subtract(this.schematicCenter).add(entry.getKey());
					if (checkWorld) {
						if (checkIfWorldIsLoaded) {
							Chunk chunk = spot.world().getChunkFromBlockCoords(setPos.xi(), setPos.zi());
							if (!chunk.isChunkLoaded) {
								continue;
							}
						}
						Block checkID = setPos.getBlock(spot.world());
						int checkMeta = setPos.getBlockMetadata(spot.world());
						if (checkID == block && checkMeta == meta) {
							continue;
						}
					}

					blockMap.put(setPos, new ItemStack(block, 1, meta));
				}
			}
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		if (!init) {
			this.init();
		}
		NBTTagCompound blockNBT = nbt.getCompoundTag(BLOCK_LIST_SAVE_NAME);
		if (this.schematicSize != null) {
			nbt.setInteger("sizeX", (int) this.schematicSize.x());
			nbt.setInteger("sizeY", (int) this.schematicSize.y());
			nbt.setInteger("sizeZ", (int) this.schematicSize.z());
		}
		if (this.schematicCenter != null) {
			nbt.setInteger("centerX", (int) this.schematicCenter.x());
			nbt.setInteger("centerY", (int) this.schematicCenter.y());
			nbt.setInteger("centerZ", (int) this.schematicCenter.z());
		}
		int i = 0;

		for (Entry<Vector3d, Pair<Block, Integer>> entry : block_map.entrySet()) {
			String output = "";
			Block block = entry.getValue().left();
			if (block != null && SchematicMap.BLOCK_SAVE_MAP_REV.containsKey(block)) {
				output += SchematicMap.BLOCK_SAVE_MAP_REV.get(block);
			} else {
				output += entry.getValue().left();
			}
			output += ":" + entry.getValue().right();
			output += ":" + (entry.getKey().x()) + ":" + ((int) entry.getKey().y()) + ":" + ((int) entry.getKey().z());
			blockNBT.setString("Block" + i, output);
			i++;
		}
		blockNBT.setInteger("count", i);
		nbt.setTag(BLOCK_LIST_SAVE_NAME, blockNBT);

	}

	@Override
	public void load(NBTTagCompound nbt) {
		schematicSize = new Vector3d(nbt.getInteger("sizeX"), nbt.getInteger("sizeY"), nbt.getInteger("sizeZ"));
		schematicCenter = new Vector3d(nbt.getInteger("centerX"), nbt.getInteger("centerY"), nbt.getInteger("centerZ"));
		NBTTagCompound blockDataSave = nbt.getCompoundTag(BLOCK_LIST_SAVE_NAME);

		for (int blockCount = 0; blockCount < blockDataSave.getInteger("count"); blockCount++) {
			String blockString = blockDataSave.getString("Block" + blockCount);
			String[] blockData = blockString.split(":");
			//int blockID = 0;
			Block block = null;
			int blockMeta = 0;
			Vector3d blockPostion = new Vector3d();
			if (blockData != null) {
				try {
					if (blockData.length > 0) {
						if (SchematicMap.BLOCK_SAVE_MAP.containsKey(blockData[0])) {
							block = SchematicMap.BLOCK_SAVE_MAP.get(blockData[0]);
						} else {
							block = Block.getBlockById(Integer.parseInt(blockData[0])); //TODO: Fix up, wrong implementation
						}

					}
					if (blockData.length > 1) {
						blockMeta = Integer.parseInt(blockData[1]);
					}
					if (blockData.length > 2) {
						blockPostion.x(Integer.parseInt(blockData[2]));
					}
					if (blockData.length > 3) {
						blockPostion.y(Integer.parseInt(blockData[3]));
					}
					if (blockData.length > 4) {
						blockPostion.z(Integer.parseInt(blockData[4]));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.block_map.put(blockPostion, new Pair<Block, Integer>(block, blockMeta));
			}
		}

		if (!init) {
			this.init();
		}

	}

	public void getFromResourceFolder(String fileName) {
		try {
			this.load(CompressedStreamTools.readCompressed(SchematicMap.class.getResource("/assets/artillects/schematics/" + fileName + ".dat").openStream()));
			this.name = fileName;
			this.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToBaseDirectory(String fileName) {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			this.save(nbt);
			NBTUtility.saveData(new File(NBTUtility.getBaseDirectory(), "schematics"), fileName, nbt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SchematicMap loadWorldSelection(World world, Vector3d pos, Vector3d pos2) {
		int deltaX, deltaY, deltaZ;
		Vector3d start = new Vector3d(pos.x() > pos2.x() ? pos2.x() : pos.x(), pos.y() > pos2.y() ? pos2.y() : pos.y(), pos.z() > pos2.z() ? pos2.z() : pos.z());

		SchematicMap sch = new SchematicMap();
		if (pos.x() < pos2.x()) {
			deltaX = (int) (pos2.x() - pos.x() + 1);
		} else {
			deltaX = (int) (pos.x() - pos2.x() + 1);
		}
		if (pos.y() < pos2.y()) {
			deltaY = (int) (pos2.y() - pos.y() + 1);
		} else {
			deltaY = (int) (pos.y() - pos2.y() + 1);
		}
		if (pos.z() < pos2.z()) {
			deltaZ = (int) (pos2.z() - pos.z() + 1);
		} else {
			deltaZ = (int) (pos.z() - pos2.z() + 1);
		}
		sch.schematicSize = new Vector3d(deltaX, deltaY, deltaZ);
		for (int x = 0; x < deltaX; ++x) {
			for (int y = 0; y < deltaY; ++y) {
				for (int z = 0; z < deltaZ; ++z) {
					Block block = world.getBlock((int) start.x() + x, (int) start.y() + y, (int) start.z() + z);
					int blockMeta = world.getBlockMetadata((int) start.x() + x, (int) start.y() + y, (int) start.z() + z);
					sch.block_map.put(new Vector3d(x, y, z), new Pair<Block, Integer>(block, blockMeta));
				}
			}
		}
		return sch;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public HashMap<Vector3d, Pair<Block, Integer>> getStructure(Direction dir, int size) {
		return this.block_map;
	}
}
