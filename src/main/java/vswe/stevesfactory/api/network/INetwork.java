package vswe.stevesfactory.api.network;

import net.minecraft.util.math.BlockPos;

import java.util.Set;

public interface INetwork {

    Set<BlockPos> getConnectedCables();

}
