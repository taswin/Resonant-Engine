package resonantengine.lib.utility.path;

import net.minecraftforge.common.util.ForgeDirection;
import nova.core.util.transform.Vector3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * An advanced version of pathfinding to find the shortest path between two points. Uses the A*
 * Pathfinding algorithm.
 *
 * @author Calclavia
 */
public class PathfinderAStar extends Pathfinder
{
	/**
	 * The set of tentative permissions to be evaluated, initially containing the start node
	 */
	public Set<Vector3d> openSet;

	/**
	 * The map of navigated permissions storing the data of which position came from which in the format
	 * of: X came from Y.
	 */
	public HashMap<Vector3d, Vector3d> navigationMap;

	/**
	 * Score values, used to determine the score for a path to evaluate how optimal the path is.
	 * G-Score is the cost along the best known path while F-Score is the total cost.
	 */
	public HashMap<Vector3d, Double> gScore, fScore;

	/**
	 * The node in which the pathfinder is trying to reach.
	 */
	public Vector3d goal;

	public PathfinderAStar(IPathCallBack callBack, Vector3d goal)
	{
		super(callBack);
		this.goal = goal;
	}

	@Override
	public boolean findNodes(Vector3d start)
	{
		this.reset();
		this.openSet.add(start);
		this.gScore.put(start, 0d);
		this.fScore.put(start, this.gScore.get(start) + getHeuristicEstimatedCost(start, this.goal));

		while (!this.openSet.isEmpty())
		{
			// Current is the node in openset having the lowest f_score[] value
			Vector3d currentNode = null;

			double lowestFScore = 0;

			for (Vector3d node : this.openSet)
			{
				if (currentNode == null || this.fScore.get(node) < lowestFScore)
				{
					currentNode = node;
					lowestFScore = this.fScore.get(node);
				}
			}

			if (currentNode == null)
			{
				break;
			}

			if (this.callBackCheck.onSearch(this, start, currentNode))
			{
				return false;
			}

			if (currentNode.equals(this.goal))
			{
				this.results = reconstructPath(this.navigationMap, goal);
				return true;
			}

			this.openSet.remove(currentNode);
			this.closedSet.add(currentNode);

			for (Vector3d neighbor : getNeighborNodes(currentNode))
			{
				double tentativeGScore = this.gScore.get(currentNode) + currentNode.distance(neighbor);

				if (this.closedSet.contains(neighbor))
				{
					if (tentativeGScore >= this.gScore.get(neighbor))
					{
						continue;
					}
				}

				if (!this.openSet.contains(neighbor) || tentativeGScore < this.gScore.get(neighbor))
				{
					this.navigationMap.put(neighbor, currentNode);
					this.gScore.put(neighbor, tentativeGScore);
					this.fScore.put(neighbor, gScore.get(neighbor) + getHeuristicEstimatedCost(neighbor, goal));
					this.openSet.add(neighbor);
				}
			}
		}

		return false;
	}

	@Override
	public Pathfinder reset()
	{
		this.openSet = new HashSet<Vector3d>();
		this.navigationMap = new HashMap<Vector3d, Vector3d>();
		this.gScore = new HashMap<Vector3d, Double>();
		this.fScore = new HashMap<Vector3d, Double>();
		return super.reset();
	}

	/**
	 * A recursive function to back track and find the path in which we have analyzed.
	 */
	public List<Vector3d> reconstructPath(HashMap<Vector3d, Vector3d> nagivationMap, Vector3d current_node)
	{
		List<Vector3d> path = new LinkedList<Vector3d>();
		path.add(current_node);

		if (nagivationMap.containsKey(current_node))
		{
			path.addAll(reconstructPath(nagivationMap, nagivationMap.get(current_node)));
			return path;
		}
		else
		{
			return path;
		}
	}

	/**
	 * @return An estimated cost between two points.
	 */
	public double getHeuristicEstimatedCost(Vector3d start, Vector3d goal)
	{
		return start.distance(goal);
	}

	/**
	 * @return A Set of neighboring Vector3d positions.
	 */
	public Set<Vector3d> getNeighborNodes(Vector3d vector)
	{
		if (this.callBackCheck != null)
		{
			return this.callBackCheck.getConnectedNodes(this, vector);
		}
		else
		{
			Set<Vector3d> neighbors = new HashSet<Vector3d>();

			for (int i = 0; i < 6; i++)
			{
				neighbors.add(vector.clone().add(ForgeDirection.getOrientation(i)));
			}

			return neighbors;
		}
	}
}
