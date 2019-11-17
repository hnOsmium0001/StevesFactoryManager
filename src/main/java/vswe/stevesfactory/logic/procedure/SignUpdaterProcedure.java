package vswe.stevesfactory.logic.procedure;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

// TODO
public class SignUpdaterProcedure extends AbstractProcedure {

    public SignUpdaterProcedure() {
        super(Procedures.SIGN_UPDATER.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<SignUpdaterProcedure> createFlowComponent() {
        FlowComponent<SignUpdaterProcedure> f = FlowComponent.of(this);
        return f;
    }
}
