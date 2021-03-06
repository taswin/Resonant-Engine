package resonant.junit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;
import resonant.junit.world.FakeRegistryNamespaced;
import resonant.lib.utility.ReflectionUtility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** In-Between registry for anything that a mod normally needs to register with MC or Forge.
 * This should only be used for JUnit tests and never for actual mod.
 * Created by robert on 11/23/2014.
 */
public class TestRegistry
{
    private static List<IFuelHandler> fuelHandlers = Lists.newArrayList();
    private static boolean blocksInit = false;
    private static int nextID = 500;

    public static void init()
    {
        if (!blocksInit)
        {
            //Sets registry to avoid trigger creating of ModClassLoader
            try
            {
                ReflectionUtility.setMCField(Block.class, null, "blockRegistry", new FakeRegistryNamespaced());
                ReflectionUtility.setMCField(Item.class, null, "itemRegistry", new FakeRegistryNamespaced());
                Block.registerBlocks();
                Item.registerItems();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (NoSuchFieldException e)
            {
                e.printStackTrace();
            }
            blocksInit = true;
        }
    }
    /**
     * Register a block with the specified mod specific name
     * @param block The block to register
     * @param name The mod-unique name to register it as, will get prefixed by your modid.
     */
    public static Block registerBlock(Block block, String name)
    {
        return registerBlock(block, ItemBlock.class, name);
    }

    /**
     * Register a block with the world, with the specified item class and block name
     * @param block The block to register
     * @param itemclass The item type to register with it : null registers a block without associated item.
     * @param name The mod-unique name to register it as, will get prefixed by your modid.
     */
    public static Block registerBlock(Block block, Class<? extends ItemBlock> itemclass, String name)
    {
        init();

        if(!Block.blockRegistry.containsKey(name))
        {
            int id = nextID++;
            Block.blockRegistry.addObject(id, name, block);
            try
            {
                Constructor con = itemclass.getConstructor(Block.class);
                ItemBlock itemBlock = (ItemBlock) con.newInstance(block);
                if(itemBlock != null)
                {
                    Item.itemRegistry.addObject(id, name, itemBlock);
                }
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            throw new IllegalArgumentException("Block is already registered to " + name);
        }
        return Block.getBlockFromName(name);
    }
}
