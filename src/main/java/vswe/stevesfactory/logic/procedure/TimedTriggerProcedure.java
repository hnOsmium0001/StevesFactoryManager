package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import vswe.stevesfactory.api.logic.ICommandGraph;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.execution.ITickable;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.IntervalMenu;

public class TimedTriggerProcedure extends AbstractProcedure implements ITickable {

    private int tickCounter = 0;
    public int interval = 20;

    public TimedTriggerProcedure(INetworkController controller) {
        super(Procedures.TRIGGER.getFactory(), controller, 0, 1);
    }

    @Override
    public void execute(IExecutionContext context) {
        context.push(successors()[0]);
    }

    @Override
    public void tick() {
        if (tickCounter >= interval) {
            getGraph().execute();
            tickCounter = 0;
        } else {
            tickCounter++;
        }
    }

    public int getInterval() {
        return interval;
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
