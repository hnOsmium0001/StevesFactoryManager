package vswe.stevesfactory.api.logic;

import net.minecraft.item.Item;
import net.minecraft.world.IWorld;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.item.ItemBufferElement;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * A one-use only context object for program execution data storage.
 */
// TODO variables, heads, tails, subsequences (functions), pid, forking
public interface IExecutionContext {

    INetworkController getController();

    IWorld getControllerWorld();

    void push(@Nullable IProcedure frame);

    Map<Item, ItemBufferElement> getItemBufferElements();
}
