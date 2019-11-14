package vswe.stevesfactory.logic.procedure;

import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

// TODO
public class BUDTriggerProcedure extends AbstractProcedure {

    public BUDTriggerProcedure() {
        super(Procedures.BUD_TRIGGER.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    public FlowComponent<BUDTriggerProcedure> createFlowComponent() {
        FlowComponent<BUDTriggerProcedure> f = FlowComponent.of(this);
        return f;
    }
}
