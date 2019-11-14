package vswe.stevesfactory.logic.execution;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import vswe.stevesfactory.api.item.IItemBufferElement;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.item.CraftingBufferElement;
import vswe.stevesfactory.logic.item.DirectBufferElement;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * A common execution context implementation for procedures.
 * <p>
 * This should only exist on server side. Usages on client usually indicates a design problem.
 */
public class ProcedureExecutor implements IExecutionContext {

    private static final Object2IntMap<Class<? extends IItemBufferElement>> orderAssociation = new Object2IntOpenHashMap<>();
    private static int nextOrder = 0;

    public static void registerBufferType(Class<? extends IItemBufferElement> type) {
        orderAssociation.put(type, nextOrder++);
    }

    static {
        registerBufferType(CraftingBufferElement.class);
        registerBufferType(DirectBufferElement.class);
    }

    private final INetworkController controller;
    private final World world;

    private Deque<IProcedure> executionStack = new ArrayDeque<>();
    @SuppressWarnings("unchecked")
    private Map<Item, IItemBufferElement>[] itemBufferElements = new IdentityHashMap[orderAssociation.size()];

    public ProcedureExecutor(INetworkController controller, World world) {
        this.controller = controller;
        this.world = world;
    }

    @Override
    public INetworkController getController() {
        return controller;
    }

    @Override
    public World getControllerWorld() {
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

    private Map<Item, IItemBufferElement> getOrCreateBuffers(int index) {
        if (itemBufferElements[index] != null) {
            return itemBufferElements[index];
        }
        Map<Item, IItemBufferElement> map = new IdentityHashMap<>();
        itemBufferElements[index] = map;
        return map;
    }

    @Override
    public <T extends IItemBufferElement> Map<Item, T> getItemBuffers(Class<T> type) {
        int index = orderAssociation.getInt(type);
        // Returns checked type -> entry put is checked too
        @SuppressWarnings("unchecked") Map<Item, T> map = (Map<Item, T>) getOrCreateBuffers(index);
        return map;
    }

    @Override
    public void forEachItemBuffer(BiConsumer<Item, IItemBufferElement> lambda) {
        for (Map<Item, IItemBufferElement> buffers : itemBufferElements) {
            if (buffers == null) {
                continue;
            }
            buffers.forEach(lambda);
        }
    }

    private void cleanup() {
        for (Map<Item, IItemBufferElement> buffers : itemBufferElements) {
            if (buffers == null) {
                continue;
            }
            for (Map.Entry<Item, IItemBufferElement> entry : buffers.entrySet()) {
                IItemBufferElement element = entry.getValue();
                element.cleanup();
            }
        }
    }
}
