package resonant.lib.prefab.tile;

import net.minecraft.block.material.Material;
import net.minecraftforge.common.util.ForgeDirection;
import resonant.api.tile.INodeProvider;
import resonant.api.tile.node.INode;
import resonant.lib.grid.branch.NodeBranchPart;
import resonant.lib.prefab.tile.spatial.SpatialTile;

import java.util.List;

/**
 * Basic wire mainly for testing RE internal functionality, can be
 * used as a template for creating wire, pipes, or grid based blocks.
 * @author Darkguardsman
 */
public class TileConductor extends SpatialTile implements INodeProvider
{
    @Override public Material material()
    {
        return null;
    }

    public NodeBranchPart node;

    public TileConductor()
    {
        super(Material.circuits);
    }

    @Override
    public <N extends INode> N getNode(Class<? extends N> nodeType, ForgeDirection from)
    {
        //TODO fix later when implement junk tests
        return (N) getNode();
    }

    public void getNodes(List<INode> nodes)
    {
        nodes.add(getNode());
    }

    public NodeBranchPart getNode()
    {
        if (node == null)
        {
            node = new NodeBranchPart(this);
        }
        return node;
    }
}