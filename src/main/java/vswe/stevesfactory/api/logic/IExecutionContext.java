package vswe.stevesfactory.api.logic;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import vswe.stevesfactory.api.item.IItemBufferElement;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A one-use only context object for program execution data storage.
 */
// TODO variables, subsequences (functions)
public interface IExecutionContext {

    INetworkController getController();

    World getControllerWorld();

    void push(@Nullable IProcedure frame);

    <T extends IItemBufferElement> Map<Item, T> getItemBuffers(Class<T> type);

    void forEachItemBuffer(BiConsumer<Item, IItemBufferElement> lambda);
}
