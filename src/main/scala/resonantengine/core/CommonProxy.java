package resonantengine.core;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import resonantengine.lib.mod.AbstractProxy;

/**
 * The Resonant Engine common proxy
 *
 * @author Calclavia
 */
public class CommonProxy extends AbstractProxy
{
	public boolean isPaused()
	{
		return false;
	}

	public EntityPlayer getClientPlayer()
	{
		return null;
	}

	@Override
	public void init()
	{
		if (Loader.isModLoaded("UniversalElectricity"))
		{
			throw new RuntimeException("UniversalElectricity is already contained within Resonant Engine and shouldn't be installed as a standalone");
		}
	}

	public void registerTileEntity(String name, String prefix, Class<? extends TileEntity> clazz)
	{
		GameRegistry.registerTileEntityWithAlternatives(clazz, prefix + name, name);
	}

	public void registerDummyRenderer(Class<? extends TileEntity> clazz)
	{

	}
}
