package resonant.core.asm;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import resonant.lib.event.ChunkModifiedEvent.ChunkSetBlockEvent;

/**
 * @author Calclavia
 */
public class StaticForwarder
{
	public static void chunkSetBlockEvent(Chunk chunk, int x, int y, int z, Block block, int blockMetadata)
	{
		MinecraftForge.EVENT_BUS.post(new ChunkSetBlockEvent(chunk, x, y, z, block, blockMetadata));
	}
}
