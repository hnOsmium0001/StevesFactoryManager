package vswe.stevesfactory.api.network;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.api.network.IConnectable.ConnectionType;

import java.util.Iterator;

public final class Connections implements Iterable<Pair<Direction, ConnectionType>> {

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
    public Iterator<Pair<Direction, ConnectionType>> iterator() {
        return connections();
    }

    public Iterator<Pair<Direction, ConnectionType>> connections() {
        return new AbstractIterator<Pair<Direction, ConnectionType>>() {
            private int last = 0;

            @Override
            protected Pair<Direction, ConnectionType> computeNext() {
                switch (last++) {
                    case 0:
                        return Pair.of(Direction.DOWN, down);
                    case 1:
                        return Pair.of(Direction.UP, up);
                    case 2:
                        return Pair.of(Direction.NORTH, north);
                    case 3:
                        return Pair.of(Direction.SOUTH, south);
                    case 4:
                        return Pair.of(Direction.WEST, west);
                    case 5:
                        return Pair.of(Direction.EAST, east);
                    default:
                        return endOfData();
                }
            }
        };
    }

    public Iterator<Pair<Direction, ConnectionType>> connections(ConnectionType filter) {
        return new AbstractIterator<Pair<Direction, ConnectionType>>() {
            private final Iterator<Pair<Direction, ConnectionType>> all = connections();

            @Override
            protected Pair<Direction, ConnectionType> computeNext() {
                while (all.hasNext()) {
                    Pair<Direction, ConnectionType> current = all.next();
                    if (current.getRight() == filter) {
                        return current;
                    }
                }
                return endOfData();
            }
        };
    }

}
