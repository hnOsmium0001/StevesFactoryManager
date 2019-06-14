package vswe.stevesfactory.utils;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;

public final class VectorHelper {

    private VectorHelper() {
    }

    /**
     * Cached immutable list of {@link Direction}s for helping reduce memory usage.
     */
    public static ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());

    public static Iterable<BlockPos> neighbors(BlockPos center) {
        return () -> neighborsIterator(center);
    }

    public static Iterator<BlockPos> neighborsIterator(BlockPos center) {
        return new AbstractIterator<BlockPos>() {
            private int index = 0;

            @Override
            protected BlockPos computeNext() {
                return center.offset(DIRECTIONS.get(index++));
            }
        };
    }

}
