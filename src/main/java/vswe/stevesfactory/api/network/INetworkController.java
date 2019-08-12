package vswe.stevesfactory.api.network;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import vswe.stevesfactory.api.logic.CommandGraph;

import java.util.Collection;
import java.util.Set;

public interface INetworkController {

    DimensionType getDimension();

    BlockPos getPos();

    IWorld getWorld();

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

    Collection<CommandGraph> getCommandGraphs();

    @CanIgnoreReturnValue
    boolean addCommandGraph(CommandGraph graph);

    @CanIgnoreReturnValue
    boolean addCommandGraphs(Collection<CommandGraph> graphs);

    @CanIgnoreReturnValue
    boolean removeCommandGraph(CommandGraph graph);

    void removeAllCommandGraphs();

    /**
     * Sync command graph data.
     */
    void sync();
}
