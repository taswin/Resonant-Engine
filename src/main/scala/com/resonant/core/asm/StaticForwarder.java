package com.resonant.core.asm;

import com.resonant.core.api.event.ChunkModifiedEvent.ChunkSetBlockEvent;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author Calclavia
 */
public class StaticForwarder {
	public static void chunkSetBlockEvent(Chunk chunk, int x, int y, int z, Block block, int blockMetadata) {
		//TODO: Switch to better event
		MinecraftForge.EVENT_BUS.post(new ChunkSetBlockEvent(chunk, x, y, z, block, blockMetadata));
	}
}
