package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.hooks.ITimedTask;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import javax.annotation.Nullable;

public class TimedTriggerProcedure extends AbstractProcedure implements ITimedTask {

    private int interval;

    public TimedTriggerProcedure(INetworkController controller) {
        super(Procedures.TRIGGER.getFactory(), controller, 1);
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        return nexts()[0];
    }

    @Override
    public void setController(INetworkController controller) {
        super.setController(controller);
        controller.getTypedHooks(ITimedTask.class)
                .stream()
                .findFirst()
                .ifPresent(hook -> hook.subscribe(this));
    }

    @Override
    public int getInterval() {
        return interval;
    }

    @Override
    public void run(INetworkController controller) {
        controller.beginExecution(this);
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.putInt("Interval", interval);
        return tag;
    }

    public static TimedTriggerProcedure deserialize(CompoundNBT tag) {
        TimedTriggerProcedure p = new TimedTriggerProcedure(getController(tag));
        p.interval = tag.getInt("Interval");
        return p;
    }

    public static FlowComponent createFlowComponent(TimedTriggerProcedure procedure) {
        return new FlowComponent(1, 1) {};
    }
}
