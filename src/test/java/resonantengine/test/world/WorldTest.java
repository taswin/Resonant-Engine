package resonantengine.test.world;

import junit.framework.TestCase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import resonantengine.core.junit.world.FakeWorld;

/**
 * Created by robert on 11/13/2014.
 */
public class WorldTest extends TestCase
{
    World world = null;

    @Override
    protected void setUp() throws Exception
    {
        world = new FakeWorld();
    }

    public void testBlockRegistry()
    {
        Object block = Block.blockRegistry.getObject("sand");
        assertNotNull(block);
        assertEquals(Block.getIdFromBlock((Block) block), 12);
    }

    public void testCreation()
    {
        assertNotNull("Failed to create world", world);
    }

    public void testNullPlacement()
    {
        try
        {
            world.setBlock(0, 0, 0, null);
            fail("World didn't catch null block");
        }
        catch (NullPointerException e)
        {
            //This should be thrown :)
        }
    }

    public void testBlockPlacement()
    {
        if (Blocks.sand != null)
        {
            world.setBlock(0, 0, 0, Blocks.sand);
            Block block = world.getBlock(0, 0, 0);
            assertEquals("World.getBlock() failed ", Blocks.sand, block);
        } else
        {
            fail("Blocks.sand is null");
        }
    }

    public void testTilePlacement()
    {
        if (Blocks.chest != null)
        {
            world.setBlock(0, 0, 0, Blocks.chest);
            Block block = world.getBlock(0, 0, 0);
            assertEquals("World.getBlock() failed ", Blocks.chest, block);
            if (!(world.getTileEntity(0, 0, 0) instanceof TileEntityChest))
            {
                fail("world.getTileEntity() returned the wrong tile\n" + world.getTileEntity(0, 0, 0) + "  should equal TileEntityChest");
            }
        } else
        {
            fail("Blocks.chest is null");
        }
    }
}
