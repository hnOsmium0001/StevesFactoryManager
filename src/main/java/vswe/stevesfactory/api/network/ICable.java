package vswe.stevesfactory.api.network;

import net.minecraft.util.math.BlockPos;

/**
 * A block that is a part of a network, such as cables.
 */
public interface ICable {

    BlockPos getPos();

//    /**
//     * Get a list of cables that is directly neighbors of this block. Specifically, all elements of the returned list must must have their
//     * coordinates one of the returns values of {@link BlockPos#offset(Direction)}.
//     */
//    List<ICable> getNeighbors();

    /**
     * This block will only be recognized if this method returns {@code true}. This could be used at situations such as making a block
     * become cables only if the player upgraded it.
     */
    boolean isCable();

    Connections getConnectionStatus();

    void updateConnections();

    /**
     * Triggers wen this cable components joins a network. Cable implementations can add hooks and capabilities to this network.
     * <p>
     * Additionally it is ok to store a reference to the joined networks. However usually this is not needed.
     */
    void onJoinNetwork(INetwork network);

    /**
     * Triggers when this cable component leaves a network. Cable implementations can should removed added hooks and capabilities from the
     * network.
     */
    void onLeaveNetwork(INetwork network);

}
