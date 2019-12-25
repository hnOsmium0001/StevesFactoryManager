package vswe.stevesfactory.logic.procedure;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.ModProcedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

// TODO
public class FunctionCallProcedure extends AbstractProcedure {

    public FunctionCallProcedure() {
        super(ModProcedures.functionCall);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<FunctionCallProcedure> createFlowComponent() {
        return FlowComponent.of(this);
    }
}
