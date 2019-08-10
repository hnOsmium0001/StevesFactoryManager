package vswe.stevesfactory.logic.execution;

import net.minecraft.world.IWorld;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A common execution context implementation for procedures.
 * <p>
 * This should only exist on server side. Usages on client probably indicates a design problem.
 */
public class ProcedureExecutor implements IExecutionContext {

    private final INetworkController controller;
    private final IWorld world;

    private Deque<IProcedure> executionStack = new ArrayDeque<>();

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

    @Override
    public void push(@Nullable IProcedure frame) {
        if (frame != null) {
            executionStack.push(frame);
        }
    }

    public void start(IProcedure hat) {
        executionStack.push(hat);
        while (!executionStack.isEmpty()) {
            executionStack.poll().execute(this);
        }
    }
}
