package universalelectricity.core.net;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import universalelectricity.api.net.IUpdate;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/** A ticker to update all networks. Register your custom network here to have it ticked by Universal
 * Electricity.
 * 
 * @author Calclavia */
public class NetworkTickHandler implements ITickHandler
{
    public static final NetworkTickHandler INSTANCE = new NetworkTickHandler();

    private final LinkedHashSet<WeakReference<IUpdate>> toAddUpdaters = new LinkedHashSet<WeakReference<IUpdate>>();
    private final LinkedHashSet<WeakReference<IUpdate>> updaters = new LinkedHashSet<WeakReference<IUpdate>>();

    /** For queuing Forge events to be invoked the next tick. */
    private final LinkedHashSet<Event> toAddEvents = new LinkedHashSet<Event>();
    private final LinkedHashSet<Event> queuedEvents = new LinkedHashSet<Event>();

    private boolean markClear;
    private boolean markQueueClear;

    public static void addNetwork(IUpdate updater)
    {
        synchronized (INSTANCE.toAddUpdaters)
        {
            if (!INSTANCE.updaters.contains(updater))
            {
                INSTANCE.toAddUpdaters.add(new WeakReference<IUpdate>(updater));
            }
        }
    }

    public static void queueEvent(Event event)
    {
        synchronized (INSTANCE.toAddEvents)
        {
            if (!INSTANCE.queuedEvents.contains(event))
            {
                INSTANCE.toAddEvents.add(event);
            }
        }
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        /** Network */
        this.updaters.addAll(new HashSet<WeakReference<IUpdate>>(this.toAddUpdaters));
        this.toAddUpdaters.clear();

        Iterator<WeakReference<IUpdate>> networkIt = this.updaters.iterator();

        while (networkIt.hasNext())
        {
            IUpdate network = networkIt.next().get();
            if (network == null)
            {
                networkIt.remove();
            }
            else
            {
                if (network.canUpdate())
                {
                    network.update();
                }

                if (!network.continueUpdate())
                {
                    networkIt.remove();
                }
            }
        }

        /** Events */
        this.queuedEvents.addAll(this.toAddEvents);
        this.toAddEvents.clear();

        Iterator<Event> eventIt = this.queuedEvents.iterator();

        while (eventIt.hasNext())
        {
            MinecraftForge.EVENT_BUS.post(eventIt.next());
            eventIt.remove();
        }

        if (markClear)
        {
            updaters.clear();
            queuedEvents.clear();
            markClear = false;
        }

        if (markQueueClear)
        {
            toAddUpdaters.clear();
            toAddEvents.clear();
            markQueueClear = false;
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel()
    {
        return "Universal Electricity Ticker";
    }

    public void clearNetworks()
    {
        markClear = true;
    }

    public void clearQueues()
    {
        markQueueClear = true;
    }

}
