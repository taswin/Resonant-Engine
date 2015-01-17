package resonant.api;

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
	 * Returns the update rate of the updater. How many times per second should it update?
	 * This method is only useful for threaded tickers. By default, update rate should be 20 per second.
	 * @return An integer representing how many times update is called every single second. A value of zero will remove this updater from the list.
	 */
	public int updateRate();
}
