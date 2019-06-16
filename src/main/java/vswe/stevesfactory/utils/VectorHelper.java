package vswe.stevesfactory.utils;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Iterator;

public final class VectorHelper {

    private VectorHelper() {
    }

    /**
     * Cached immutable list of {@link Direction}s for helping reduce memory usage.
     */
    public static final ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());

    public static Iterable<BlockPos> neighbors(BlockPos center) {
        return () -> neighborsIterator(center);
    }

    public static Iterator<BlockPos> neighborsIterator(BlockPos center) {
        return new AbstractIterator<BlockPos>() {
            private int index = 0;

            @Override
            protected BlockPos computeNext() {
                if (index >= DIRECTIONS.size()) {
                    return endOfData();
                }
                return center.offset(DIRECTIONS.get(index++));
            }
        };
    }

    // Inlined because apparently applications of pure function with constants are not considered constants
    private static final int DOWN_BITS = 0b010100; // calculateDirectionBits(0, -1, 0)
    private static final int UP_BITS = 0b010110; // calculateDirectionBits(0, 1, 0)
    private static final int NORTH_BITS = 0b010001; // calculateDirectionBits(0, 0, -1)
    private static final int SOUTH_BITS = 0b011001; // calculateDirectionBits(0, 0, 1)
    private static final int WEST_BITS = 0b000101; // calculateDirectionBits(-1, 0, 0)
    private static final int EAST_BITS = 0b100101; // calculateDirectionBits(1, 0, 0)

    private static int calculateDirectionBits(int dx, int dy, int dz) {
        // Add 1 to make all offsets positive to avoid dealing with complement bits
        return ((dx + 1) << 4) | ((dz + 1) << 2) | (dy + 1);
    }

    @Nullable
    public static Direction relativeDirection(BlockPos center, BlockPos neighbor) {
        int dx = neighbor.getX() - center.getX();
        int dy = neighbor.getY() - center.getY();
        int dz = neighbor.getZ() - center.getZ();
        switch (calculateDirectionBits(dx, dy, dz)) {
            case DOWN_BITS:
                return Direction.DOWN;
            case UP_BITS:
                return Direction.UP;
            case NORTH_BITS:
                return Direction.NORTH;
            case SOUTH_BITS:
                return Direction.SOUTH;
            case WEST_BITS:
                return Direction.WEST;
            case EAST_BITS:
                return Direction.EAST;
        }
        return null;
    }

}
