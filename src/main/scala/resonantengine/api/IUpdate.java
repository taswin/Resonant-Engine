package resonantengine.api;

/**
 * Applies to objects that can receive update tick calls.
 *
 * @Calclavia
 */
public interface IUpdate
{
	/**
	 * Updates the network. Called by the {UpdateTicker}.
	 *
	 * @param deltaTime - The time taken in seconds between the last update.
	 */
	public void update(double deltaTime);

	/**
	 * Returns how many milliseconds there should be in successive updates. This method is only useful for threaded tickers.
	 * By default, update period should be 50 milliseconds (20 updates per second).
	 * @return An integer representing how many milliseconds between each update. A value of zero will remove this updater from the list.
	 */
	public int updatePeriod();
}
