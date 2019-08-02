package vswe.stevesfactory.logic.procedure;

import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;

public class ItemTransferProcedure extends AbstractProcedure {

    public ItemTransferProcedure(INetworkController controller) {
        super(controller, 1);
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        return nexts()[0];
    }
}
