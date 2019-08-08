package vswe.stevesfactory.logic.hooks;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import vswe.stevesfactory.api.manager.ITriggerHook;
import vswe.stevesfactory.api.network.INetworkController;

public class IntervalTriggerHook implements ITriggerHook<ITimedTask> {

    private Object2LongMap<ITimedTask> tasks = new Object2LongOpenHashMap<>();

    @Override
    public void subscribe(ITimedTask task) {
        tasks.put(task, 0);
    }

    @Override
    public void unsubscribe(ITimedTask task) {
        tasks.removeLong(task);
    }

    @Override
    public void tick(INetworkController controller) {
        for (Object2LongMap.Entry<ITimedTask> entry : tasks.object2LongEntrySet()) {
            long v = entry.getLongValue();
            if (v >= entry.getKey().getInterval()) {
                entry.setValue(0);
                entry.getKey().run(controller);
            } else {
                entry.setValue(v + 1);
            }
        }
    }
}
