package vswe.stevesfactory.api.network;

import net.minecraft.util.math.BlockPos;

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

    void addLinksFor(INetworkController controller);
}
