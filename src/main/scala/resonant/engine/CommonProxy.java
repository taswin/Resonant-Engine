package resonant.engine;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import resonant.lib.mod.AbstractProxy;
import resonant.lib.network.Synced;
import resonant.lib.network.discriminator.PacketAnnotationManager;
import resonant.lib.utility.ReflectionUtility;

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

		try
		{
			//Auto register any tile that contains these annotations
			if (ReflectionUtility.getAllMethods(clazz, Synced.class, Synced.SyncedInput.class, Synced.SyncedOutput.class).size() > 0)
			{
				PacketAnnotationManager.INSTANCE.register(clazz);
			}
		}
		catch (Throwable e)
		{
			Reference.logger().error("Error checking if " + clazz.getSimpleName() + ".class contains @Synced methods this may cause packet update issues");
		}
	}

	public void registerDummyRenderer(Class<? extends TileEntity> clazz)
	{

	}
}
