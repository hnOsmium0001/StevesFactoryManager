package vswe.stevesfactory.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesfactory.api.Connections;
import vswe.stevesfactory.api.IConnectable;
import vswe.stevesfactory.api.IConnectable.ConnectionType;

public final class ConnectionHelper {

    private ConnectionHelper() {
    }

    public static ConnectionType getConnectionType(TileEntity tile) {
        if (tile instanceof IConnectable) {
            return ((IConnectable) tile).getConnectionType();
        }
        return ConnectionType.DEFAULT;
    }

    @CanIgnoreReturnValue
    public static Connections updateConnectionType(World world, Connections connections) {
        BlockPos center = connections.getCenter();
        for (Direction direction : Direction.values()) {
            TileEntity tile = world.getTileEntity(center.offset(direction));
            connections.set(direction, getConnectionType(tile));
        }
        return connections;
    }

}
