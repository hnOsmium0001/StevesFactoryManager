package vswe.stevesfactory.logic.execution;

import net.minecraft.world.IWorld;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;

/**
 * A common execution context implementation for procedures.
 * <p>
 * This should only exist on server side. Usages on client probably indicates a design problem.
 */
public class ProcedureExecutor implements IExecutionContext {

    private final INetworkController controller;
    private final IWorld world;

    private IProcedure current;

    public ProcedureExecutor(INetworkController controller, IWorld world) {
        this.controller = controller;
        this.world = world;
    }

    @Override
    public INetworkController getController() {
        return controller;
    }

    @Override
    public IWorld getControllerWorld() {
        return world;
    }

    public void start(IProcedure procedure) {
        this.current = procedure;
        while (true){
            IProcedure next = current.execute(this);
            if (next == null) {
                break;
            }
            current = next;
        }
    }
}
