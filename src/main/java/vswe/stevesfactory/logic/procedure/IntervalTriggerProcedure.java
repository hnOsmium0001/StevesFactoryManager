package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.execution.ProcedureExecutor;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.IntervalMenu;

public class IntervalTriggerProcedure extends AbstractProcedure {

    private int tickCounter = 0;
    public int interval = 20;

    public IntervalTriggerProcedure() {
        super(Procedures.INTERVAL_TRIGGER.getFactory(), 0, 1);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    public void tick() {
        if (tickCounter >= interval) {
            CommandGraph graph = getGraph();
            INetworkController controller = graph.getController();
            new ProcedureExecutor(controller, controller.getControllerWorld()).start(graph.getRoot());
            tickCounter = 0;
        } else {
            tickCounter++;
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.putInt("Interval", interval);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        interval = tag.getInt("Interval");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<IntervalTriggerProcedure> createFlowComponent() {
        FlowComponent<IntervalTriggerProcedure> f = FlowComponent.of(this, 0, 1);
        f.addMenu(new IntervalMenu());
        return f;
    }
}
