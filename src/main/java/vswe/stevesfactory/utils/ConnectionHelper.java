package vswe.stevesfactory.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.api.network.IConnectable;
import vswe.stevesfactory.api.network.IConnectable.LinkType;

public final class ConnectionHelper {

    private ConnectionHelper() {
    }

    public static LinkType getLinkType(TileEntity tile) {
        if (tile instanceof IConnectable) {
            return ((IConnectable) tile).getConnectionType();
        }
        return LinkType.DEFAULT;
    }

    @CanIgnoreReturnValue
    public static LinkingStatus updateLinkType(World world, LinkingStatus linkingStatus) {
        BlockPos center = linkingStatus.getCenter();
        for (Direction direction : Direction.values()) {
            TileEntity tile = world.getTileEntity(center.offset(direction));
            linkingStatus.set(direction, getLinkType(tile));
        }
        return linkingStatus;
    }

}
