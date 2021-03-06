package resonant.lib.debug;

import resonant.api.grid.IUpdate;

import java.awt.*;

/**
 * Simple label with an update method
 *
 * @author Darkguardsman
 */
@SuppressWarnings("serial")
public class UpdatedLabel extends Label implements IUpdate
{
	String start_string = "I Am a Label";

	public UpdatedLabel(String start)
	{
		super(start);
		this.start_string = start;
	}

	@Override
	public void update(double deltaSecs)
	{
		this.setText(buildLabel());
	}

	/**
	 * Recreates then returns the label's string value
	 */
	public String buildLabel()
	{
		return start_string;
	}

	@Override
	public boolean canUpdate()
	{
		return start_string != null;
	}

	@Override
	public boolean continueUpdate()
	{
		return true;
	}
}
