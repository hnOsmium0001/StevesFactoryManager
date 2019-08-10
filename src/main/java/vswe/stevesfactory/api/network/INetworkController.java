package vswe.stevesfactory.api.network;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import vswe.stevesfactory.api.logic.ICommandGraph;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.manager.ITriggerHook;
import vswe.stevesfactory.logic.graph.CommandGraph;

import java.util.Collection;
import java.util.Set;

public interface INetworkController {

    DimensionType getDimension();

    BlockPos getPos();

    IWorld getWorld();

    // TODO Not sure if I want this here
//    Set<IHook> getHooks();

    /**
     * Get a set of all trigger hooks that can accept the given task type. The returned value should not be modified. Adding hooks should be
     * done with the method {@link #addTypedHook(Class, ITriggerHook)}.
     *
     * @implSpec On server this will return a set containing all the described hooks; but on client this should return an empty set.
     */
    <T> Set<ITriggerHook<T>> getTypedHooks(Class<T> typeClass);

    /**
     * Add a hook that can accept the given task type. The provided value should show up immediately in {@link #getTypedHooks(Class)}.
     *
     * @implSpec Calling this method on client should do nothing, only on server should it modify the hooks content.
     */
    <T> void addTypedHook(Class<T> typeClass, ITriggerHook<T> hook);

    Set<BlockPos> getConnectedCables();

    /**
     * Remove a cable from the network. The removed cable will be notified with the event {@link
     * ICable#onLeaveNetwork(INetworkController)}.
     * <p>
     * This should cause the network to be updated and revalidate its cache, including connected cables.
     *
     * @implNote Implementation may assume the position contains an {@link ICable}. If it does not, it is up to the invokers to resolve the
     * problem.
     */
    void removeCable(BlockPos cable);

    /**
     * Internal storage of linked inventories by the controller.
     * <p>
     * <b>WARNING: </b> Uses of the return value of this method should <b>never</b> modify the content of this set. Instead, they should
     * use {@link #addLink(BlockPos)} and {@link #removeLink(BlockPos)} to do so. Controllers are allowed to be designed such that
     * modification to the returning set might cause breakage.
     */
    Set<BlockPos> getLinkedInventories();

    /**
     * Connect a inventory located at position to the network.
     * <p>
     * Note that this is not limited to "inventories", but anything with at least one capability.
     *
     * @return {@code true} if the position didn't exist already.
     */
    @CanIgnoreReturnValue
    boolean addLink(BlockPos pos);

    @CanIgnoreReturnValue
    boolean addLinks(Collection<BlockPos> poses);

    /**
     * @return {@code true} if the network has the position and successfully removed it.
     * @see #addLink(BlockPos)
     */
    @CanIgnoreReturnValue
    boolean removeLink(BlockPos pos);

    void removeAllLinks();

    boolean isRemoved();

    void beginExecution(IProcedure hat);

    void beginExecution(CommandGraph tree);

    @CanIgnoreReturnValue
    boolean addCommandGraph(ICommandGraph graph);

    @CanIgnoreReturnValue
    boolean addCommandGraphs(Collection<ICommandGraph> graphs);

    @CanIgnoreReturnValue
    boolean removeCommandGraph(ICommandGraph graph);

    void removeAllCommandGraphs();

    /**
     * Sync command graph data.
     */
    void sync();
}
