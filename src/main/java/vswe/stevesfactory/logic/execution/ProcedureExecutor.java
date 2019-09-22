package vswe.stevesfactory.logic.execution;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.item.ItemBufferElement;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A common execution context implementation for procedures.
 * <p>
 * This should only exist on server side. Usages on client probably indicates a design problem.
 */
public class ProcedureExecutor implements IExecutionContext {

    private final INetworkController controller;
    private final IWorld world;

    private Deque<IProcedure> executionStack = new ArrayDeque<>();
    private Map<Item, ItemBufferElement> itemBufferElements = new HashMap<>();

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
        cleanup();
    }

    @Override
    public Map<Item, ItemBufferElement> getItemBufferElements() {
        return itemBufferElements;
    }

    private void cleanup() {
        handleExtractedItems();
    }

    private void handleExtractedItems() {
        for (Map.Entry<Item, ItemBufferElement> entry : itemBufferElements.entrySet()) {
            ItemBufferElement buffer = entry.getValue();
            if (buffer.used > 0) {
                for (Pair<IItemHandler, Integer> pair : buffer.inventories) {
                    IItemHandler handler = pair.getLeft();
                    Integer slot = pair.getRight();
                    ItemStack extracted = handler.extractItem(slot, buffer.used, false);
                    buffer.used -= extracted.getCount();
                }
            }
        }
    }
}
