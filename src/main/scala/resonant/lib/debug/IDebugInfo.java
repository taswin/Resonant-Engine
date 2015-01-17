package resonant.lib.debug;

import java.util.List;

/**
 * Applied to TileEntities that will display information in the F3 panel for debug
 *
 * @author Calclavia
 */
public interface IDebugInfo
{
	/**
	 * Returns a list of string to render in the F3 debug screen.
	 */
	List<String> getDebugInfo();
}
