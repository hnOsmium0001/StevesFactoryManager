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

}
