package resonant.lib.mod.content;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import resonant.api.items.ISimpleItemRenderer;
import resonant.engine.ResonantEngine;
import resonant.lib.prefab.tile.item.ItemBlockMetadata;
import resonant.lib.prefab.tile.spatial.BlockDummy;
import resonant.lib.prefab.tile.spatial.SpatialBlock;
import resonant.lib.render.wrapper.ItemRenderHandler;
import resonant.lib.utility.LanguageUtility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Helper class that can be used to reduce the amount of code used to handle general object registration. Handles basic block and item
 * creation threw reflection. As well follows up by setting a common creative tab, mod prefix, localization name, and texture name.
 *
 * @author DarkGuardsman, Calclavia
 */
public class ModManager
{
	public final WeakHashMap<Block, String> blocks = new WeakHashMap();
	public final WeakHashMap<Item, String> items = new WeakHashMap();

	public String modPrefix;
	public CreativeTabs defaultTab;

	public ModManager setPrefix(String modPrefix)
	{
		this.modPrefix = modPrefix;
		if (!modPrefix.endsWith(":"))
		{
			this.modPrefix += ":";
		}
		return this;
	}

	public ModManager setTab(CreativeTabs defaultTab)
	{
		this.defaultTab = defaultTab;
		return this;
	}

	/**
	 * Creates a new block based on the SpatialBlock class provided
	 * Handles most common registration and data input tasks for the creation of the block.
	 * This includes registering the block, tile, built in item & tile renders. It also inits
	 * the instance used for the block itself.
	 *
	 * @param spatialClass - class that will provide all the data to be used when creating the block.
	 * @param args         - arguments needed to create a new instance of the spatial class
	 */
	public BlockDummy newBlock(Class<? extends SpatialBlock> spatialClass, Object... args)
	{
		try
		{
			SpatialBlock spatial;

			if (args != null && args.length > 0)
			{
				List<Class> paramTypes = new ArrayList();

				for (Object arg : args)
				{
					paramTypes.add(arg.getClass());
				}

				spatial = spatialClass.getConstructor(paramTypes.toArray(new Class[paramTypes.size()])).newInstance();
			}
			else
			{
				spatial = spatialClass.newInstance();
			}

			return newBlock(spatial);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Block [" + spatialClass.getSimpleName() + "] failed to be created:", e);
		}
	}

	/**
	 * Creates a new block based on the SpatialBlock instance provided
	 * Handles most common registration and data input tasks for the creation of the block.
	 * This includes registering the block, tile, built in item & tile renders. It also inits
	 * the instance used for the block itself.
	 *
	 * @param spatial - instance of the spatial block used to provide all the data for the block
	 */
	public BlockDummy newBlock(SpatialBlock spatial)
	{
		return newBlock(spatial.name(), spatial);
	}

	/**
	 * Creates a new block based on the SpatialBlock instance provided.
	 * Handles most common registration and data input tasks for the creation of the block.
	 * This includes registering the block, tile, built in item & tile renders. It also inits
	 * the instance used for the block itself.
	 *
	 * @param spatial - instance of the spatial block used to provide all the data for the block
	 * @param name    - name the block will use for look up map, registry, texture, etc
	 */
	public BlockDummy newBlock(String name, SpatialBlock spatial)
	{
		BlockDummy block = new BlockDummy(modPrefix, defaultTab, spatial);
		spatial.setBlock(block);

		blocks.put(block, name);
		GameRegistry.registerBlock(block, spatial.itemBlock(), name);

		spatial.onInstantiate();

		if (spatial instanceof ISimpleItemRenderer)
		{
			ItemRenderHandler.register(new ItemStack(block).getItem(), (ISimpleItemRenderer) spatial);
		}

		if (spatial.tile() != null)
		{
			ResonantEngine.proxy().registerTileEntity(name, modPrefix, spatial.tile().getClass());

			if (!spatial.normalRender())
			{
				ResonantEngine.proxy().registerDummyRenderer(spatial.tile().getClass());
			}
		}

		return block;
	}

	/**
	 * Creates a new instance of the block class as long as it has a default constructor
	 */
	public Block newBlock(Class<? extends Block> blockClazz, Class<? extends ItemBlock> itemBlockClass)
	{
		return newBlock(blockClazz.getSimpleName(), blockClazz, itemBlockClass);
	}

	/**
	 * Creates a new instance of the block class as long as it has a default constructor
	 */
	public Block newBlock(Class<? extends Block> blockClazz)
	{
		return newBlock(blockClazz.getSimpleName(), blockClazz);
	}

	/**
	 * Creates a new instance of the block class as long as it has a default constructor
	 */
	public Block newBlock(String name, Class<? extends Block> blockClazz, Class<? extends ItemBlock> itemBlockClass)
	{
		try
		{
			return newBlock(name, blockClazz.newInstance(), itemBlockClass);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a new instance of the block class as long as it has a default constructor
	 */
	public Block newBlock(String name, Class<? extends Block> blockClazz)
	{
		try
		{
			return newBlock(name, blockClazz.newInstance());
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Doesn't create a new block but applies the required blockName, textureName, and registers the block
	 *
	 * @param name  - name the block will be registed with, localization, and texture name
	 * @param block - instance of the block
	 */
	public <C extends Block> C newBlock(String name, C block)
	{
		return newBlock(name, block, ItemBlock.class);
	}

	/**
	 * Doesn't create a new block but applies the required blockName, textureName, and registers the block
	 *
	 * @param name           - name the block will be registed with, localization, and texture name
	 * @param block          - instance of the block
	 * @param itemBlockClass - item block used with the block
	 */
	public <C extends Block> C newBlock(String name, C block, Class<? extends ItemBlock> itemBlockClass)
	{
		//Set block name if not present
		if (!block.getUnlocalizedName().contains(modPrefix))
		{
			block.setBlockName(modPrefix + name);
		}

		//Set texture name, reflection is used to prevent overriding the blocks existing name
		try
		{
			Field field = block.getClass().getField("field_149768_d");
			if (field == null)
			{
				field = block.getClass().getField("textureName");
			}
			field.setAccessible(true);

			if (field.get(block) == null)
			{
				block.setBlockTextureName(modPrefix + name);
			}
		}
		catch (NoSuchFieldException e)
		{
			block.setBlockTextureName(modPrefix + name);
		}
		catch (IllegalAccessException e)
		{
			block.setBlockTextureName(modPrefix + name);
		}

		// Register block with item block
		if (itemBlockClass != null)
		{
			GameRegistry.registerBlock(block, itemBlockClass, name);
		}
		else
		{
			GameRegistry.registerBlock(block, ItemBlockMetadata.class, name);
		}
		return block;
	}

	/**
	 * Creates a new Item instance using the class provided
	 *
	 * @param clazz - class to create an instance from
	 * @param args  - arguments needed to create a new instance
	 */
	public <C extends Item> C newItem(Class<C> clazz, Object... args)
	{
		return newItem(LanguageUtility.decapitalizeFirst(clazz.getSimpleName().replace("Item", "")), clazz, args);
	}

	/**
	 * Creates a new item using reflection as well runs it threw some check to activate any
	 * interface methods
	 *
	 * @param name  - name to register the item with //@param modid - mods that the item comes from
	 * @param clazz - item class
	 * @return the new item
	 */
	public <C extends Item> C newItem(String name, Class<C> clazz, Object... args)
	{
		try
		{
			Item item;

			if (args != null && args.length > 0)
			{
				List<Class> paramTypes = new ArrayList();

				for (Object arg : args)
				{
					paramTypes.add(arg.getClass());
				}

				item = clazz.getConstructor(paramTypes.toArray(new Class[paramTypes.size()])).newInstance();
			}
			else
			{
				item = clazz.getConstructor().newInstance();
			}

			return (C) newItem(name, item);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Item [" + name + "] failed to be created: " + e.getLocalizedMessage(), e.fillInStackTrace());
		}
	}

	public Item newItem(Item item)
	{
		try
		{
			return newItem(LanguageUtility.decapitalizeFirst(item.getClass().getSimpleName().replace("Item", "")), item);
		}
		catch (StringIndexOutOfBoundsException e)
		{
			System.out.println("Item: " + item + "   Class: " + item.getClass());
			throw e;
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("Item: " + item + "   Class: " + item.getClass());
			throw e;
		}
	}

	public Item newItem(String name, Item item)
	{
		if (item != null)
		{
			if (modPrefix != null)
			{
				item.setUnlocalizedName(modPrefix + name);

				if (ReflectionHelper.getPrivateValue(Item.class, item, "iconString", "field_111218_cA") == null)
				{
					item.setTextureName(modPrefix + name);
				}
			}

			if (defaultTab != null)
			{
				item.setCreativeTab(defaultTab);
			}

			items.put(item, name);
			GameRegistry.registerItem(item, name);
		}

		return item;
	}
}
