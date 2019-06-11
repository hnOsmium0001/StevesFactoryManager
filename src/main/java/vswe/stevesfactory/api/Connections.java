package vswe.stevesfactory.api;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

public final class Connections implements Iterable<BlockPos> {

    private IConnectable.ConnectionType up, down, north, south, east, west;

    public IConnectable.ConnectionType getUp() {
        return up;
    }

    public IConnectable.ConnectionType getDown() {
        return down;
    }

    public IConnectable.ConnectionType getNorth() {
        return north;
    }

    public IConnectable.ConnectionType getSouth() {
        return south;
    }

    public IConnectable.ConnectionType getEast() {
        return east;
    }

    public IConnectable.ConnectionType getWest() {
        return west;
    }

    @Override
    public Iterator<BlockPos> iterator() {
        return new AbstractIterator<BlockPos>() {
            @Override
            protected BlockPos computeNext() {
                return null;
            }
        };
    }

}
