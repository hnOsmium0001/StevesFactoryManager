package vswe.stevesfactory.api.network;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public interface INetworkController {

    Set<BlockPos> getConnectedCables();

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

    /**
     * @return {@code true} if the network has the position and successfully removed it.
     * @see #addLink(BlockPos)
     */
    @CanIgnoreReturnValue
    boolean removeLink(BlockPos pos);

}
