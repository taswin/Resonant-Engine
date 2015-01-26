package resonantengine.lib.world.edit;

import cpw.mods.fml.common.eventhandler.Event;
import resonantengine.api.TriggerCause;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import resonantengine.lib.transform.vector.VectorWorld;
import resonantengine.api.event.WorldChangeActionEvent;
import resonantengine.lib.world.edit.BlockEdit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by robert on 12/2/2014.
 */
public class WorldChangeHelper
{
    /** Called to do a change action in the world
     *
     * @param action - instance of the change
     * @param triggerCause - cause of the trigger
     * @return if the result completed, was blocked, or failed
     */
    public static ChangeResult doAction(World world, double x, double y, double z, IWorldChangeAction action, TriggerCause triggerCause)
    {
        return doAction(new VectorWorld(world, x, y, z), action, triggerCause);
    }

    /** Called to do a change action in the world
     *
     * @param loc - location in the world
     * @param action - instance of the change
     * @param triggerCause - cause of the trigger
     * @return if the result completed, was blocked, or failed
     */
    public static ChangeResult doAction(VectorWorld loc, IWorldChangeAction action, TriggerCause triggerCause)
    {
        if (action != null)
        {
            Event event = new WorldChangeActionEvent.ActionCreated(loc, action, triggerCause);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.isCanceled())
            {
                action.doEffectOther(true);
                if (action.shouldThreadAction() > 0)
                {
                    ThreadWorldChangeAction thread = new ThreadWorldChangeAction(loc, action, triggerCause);
                    thread.start();
                }
                else
                {
                    Collection<BlockEdit> effectedBlocks = getEffectedBlocks(loc, triggerCause, action);
                    if (effectedBlocks != null && !effectedBlocks.isEmpty()) ;
                    {
                        for (BlockEdit v : effectedBlocks)
                        {
                            action.handleBlockPlacement(v);
                        }
                    }
                }
                action.doEffectOther(false);
                return ChangeResult.COMPLETED;
            }
            return ChangeResult.BLOCKED;
        }
        return ChangeResult.FAILED;
    }

    /** Gets a list of blocks that change will effect
     *
     * @param vec - location in the world
     * @param triggerCause - cause
     * @param blast - action instance
     * @return list of block locations changes
     */
    public static Collection<BlockEdit> getEffectedBlocks(VectorWorld vec, TriggerCause triggerCause, IWorldChangeAction blast)
    {
        Collection<BlockEdit> effectedBlocks = blast.getEffectedBlocks();
        //Triggers an event allowing other mods to edit the block list
        MinecraftForge.EVENT_BUS.post(new WorldChangeActionEvent.FinishedCalculatingEffectEvent(vec, effectedBlocks, blast, triggerCause));

        //If we have blocks to edit then register with the event handler
        if (effectedBlocks == null)
        {
            return new ArrayList<BlockEdit>();
        }
        return effectedBlocks;
    }

    public enum ChangeResult
    {
        COMPLETED,
        FAILED,
        BLOCKED
    }
}
