package resonantengine.api.tile;

import resonantengine.api.graph.IGraph;

/**
 * Any node that is part of a grid system such as a power network
 */
public interface IGridProvider
{
    /** Sets the grid reference */
    public void setGrid(IGraph grid);

    /** Gets the grid reference */
    public IGraph getGrid();
}
