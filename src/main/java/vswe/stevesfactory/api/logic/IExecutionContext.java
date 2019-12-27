package vswe.stevesfactory.api.logic;

import com.google.common.collect.ClassToInstanceMap;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import vswe.stevesfactory.api.logic.fluid.IFluidBuffer;
import vswe.stevesfactory.api.logic.item.IItemBuffer;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A one-use only context object for program execution data storage.
 */
// TODO variables
public interface IExecutionContext {

    INetworkController getController();

    World getControllerWorld();

    void push(@Nullable IProcedure frame);

    <T extends IItemBuffer> Map<Item, T> getItemBuffers(Class<T> type);

    <T extends IFluidBuffer> Map<Fluid, T> getFluidBuffers(Class<T> type);

    ClassToInstanceMap<Object> getCustomData();

    void forEachItemBuffer(BiConsumer<Item, IItemBuffer> lambda);

    void forEachFluidBuffer(BiConsumer<Fluid, IFluidBuffer> lambda);
}
