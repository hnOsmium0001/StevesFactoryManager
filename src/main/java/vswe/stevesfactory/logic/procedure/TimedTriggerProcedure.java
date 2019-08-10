package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.hooks.ITimedTask;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.IntervalMenu;

import javax.annotation.Nullable;

public class TimedTriggerProcedure extends AbstractProcedure implements ITimedTask {

    public int interval;

    public TimedTriggerProcedure(INetworkController controller) {
        super(Procedures.TRIGGER.getFactory(), controller, 0, 1);
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        return successors()[0];
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
        // TODO should trigger the command graph instead of a plain hat procedure here?
        controller.beginExecution(this);
    }

    @Override
    public void remove() {
        super.remove();
        getController().getTypedHooks(ITimedTask.class)
                .stream()
                .findFirst()
                .ifPresent(hook -> hook.unsubscribe(this));
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.putInt("Interval", interval);
        return tag;
    }

    @Override
    public void deserialize(ICommandGraph graph, CompoundNBT tag) {
        super.deserialize(graph, tag);
        interval = tag.getInt("Interval");
    }

    public static FlowComponent<TimedTriggerProcedure> createFlowComponent(TimedTriggerProcedure procedure) {
        FlowComponent<TimedTriggerProcedure> f = Procedures.TRIGGER.factory.createWidgetDefault(procedure, 0, 1);
        f.addMenu(new IntervalMenu());
        return f;
    }
}
