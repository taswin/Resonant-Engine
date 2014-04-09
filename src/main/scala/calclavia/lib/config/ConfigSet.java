package calclavia.lib.config;

import calclavia.components.CalclaviaLoader;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;

/**
 * @author tgame14
 * @since 09/04/14
 */
public class ConfigSet extends HashSet<String>
{
	public static boolean isPostInit = false;

	@Override
	public boolean add(String c)
	{
		if (isPostInit)
		{
			//MinecraftForge.EVENT_BUS.post(new ConfigAnnotationEvent(c));

		}
		return super.add(c);
	}
}