package resonant.lib.loadable;

/**
 * Applied to loadable objects.
 *
 * @author Calclavia
 */
public interface ILoadable
{
	public void preInit();

	public void init();

	public void postInit();
}
