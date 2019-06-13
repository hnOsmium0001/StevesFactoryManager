package vswe.stevesfactory.api;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.api.IConnectable.ConnectionType;

import java.util.Iterator;

public final class Connections implements Iterable<ConnectionType> {

    private BlockPos center;
    public ConnectionType up, down, north, south, east, west;

    public Connections(BlockPos center) {
        this.center = center;
    }

    public BlockPos getCenter() {
        return center;
    }

    public ConnectionType get(Direction direction) {
        switch (direction) {
            case DOWN:
                return down;
            case UP:
                return up;
            case NORTH:
                return north;
            case SOUTH:
                return south;
            case WEST:
                return west;
            case EAST:
                return east;
        }
        throw new IllegalArgumentException("Nonexistent direction " + direction);
    }

    public void set(Direction direction, ConnectionType type) {
        switch (direction) {
            case DOWN:
                down = type;
                break;
            case UP:
                up = type;
                break;
            case NORTH:
                north = type;
                break;
            case SOUTH:
                south = type;
                break;
            case WEST:
                west = type;
                break;
            case EAST:
                east = type;
                break;
        }
    }

    /**
     * Use {@link #connections()} when not using for-each loop.
     */
    @Override
    public Iterator<ConnectionType> iterator() {
        return new AbstractIterator<ConnectionType>() {
            private int last = 0;

            @Override
            protected ConnectionType computeNext() {
                switch (last++) {
                    case 0:
                        return down;
                    case 1:
                        return up;
                    case 2:
                        return north;
                    case 3:
                        return south;
                    case 4:
                        return west;
                    case 5:
                        return east;
                    default:
                        return endOfData();
                }
            }
        };
    }

    public Iterator<ConnectionType> connections() {
        return iterator();
    }

    public Iterator<ConnectionType> connections(ConnectionType filter) {
        return new AbstractIterator<ConnectionType>() {
            private final Iterator<ConnectionType> all = connections();

            @Override
            protected ConnectionType computeNext() {
                while (all.hasNext()) {
                    ConnectionType current = all.next();
                    if (current == filter) {
                        return current;
                    }
                }
                return endOfData();
            }
        };
    }

}
