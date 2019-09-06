package vswe.stevesfactory.api.network;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * A block that is a part of a network, such as cables.
 * <p>
 * Whenever the word "connection" is used, it represents network cables unless specifically mentioned; similarly, whenever the work "link"
 * is used, it represents inventories that are <i>linked</i> to network controllers.
 */
public interface ICable {

    BlockPos getPos();

    /**
     * This block will only be recognized if this method returns {@code true}. This could be used at situations such as making a block
     * become cables only if the player upgraded it.
     */
    boolean isCable();

    /**
     * Update the links to neighboring inventories.
     */
    LinkingStatus getLinkingStatus();

    /**
     * Get a set of linked inventories.
     * <p>
     * If implementations do not support this method, they may return {@code null} to indicate so.
     */
    @Nullable
    Set<BlockPos> getNeighborInventories();

    void updateLinks();

    /**
     * Triggers wen this cable components joins a network. Cable implementations can add hooks and capabilities to this network.
     *
     * @implNote It is ok to store a reference to the joined networks.
     */
    void onJoinNetwork(INetworkController network);

    /**
     * Triggers when this cable component leaves a network. Cable implementations can should removed added hooks and capabilities from the
     * network.
     */
    void onLeaveNetwork(INetworkController network);
}
