package vswe.stevesfactory.utils;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Collection of general helper methods that doesn't worth creating an extra helper class for them.
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Cached immutable list of {@link Direction}s for helping reduce memory usage.
     */
    public static final ImmutableList<Direction> DIRECTIONS = ImmutableList.copyOf(Direction.values());

    public static boolean hasCapabilityAtAll(ICapabilityProvider provider, Capability<?> cap) {
        for (Direction direction : DIRECTIONS) {
            if (provider.getCapability(cap, direction).isPresent()) {
                return true;
            }
        }
        return provider.getCapability(cap).isPresent();
    }

    /**
     * Semantic purposing method that directs to {@link Math#max(int, int)}.
     */
    public static int lowerBound(int n, int lowerBound) {
        return Math.max(n, lowerBound);
    }

    /**
     * Semantic purposing method that directs to {@link Math#min(int, int)}.
     */
    public static int upperBound(int n, int upperBound) {
        return Math.min(n, upperBound);
    }

    /**
     * Create an {@code end-start} long int array, where the first element is {@code start}, and each element after is 1 bigger than the
     * previous element.
     */
    public static int[] rangedIntArray(int start, int end) {
        int[] result = new int[end - start];
        Arrays.setAll(result, i -> i + start);
        return result;
    }

    public static boolean invertIf(boolean bool, boolean predicate) {
        return bool ^ predicate;
    }

    /**
     * Helper for negating a method reference.
     */
    public static <T> Predicate<T> not(Predicate<T> original) {
        return original.negate();
    }

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

    public static boolean isInside(int x, int y, int mx, int my) {
        return isInside(x, y, 0, 0, mx, my);
    }

    public static boolean isInside(int x, int y, int bx1, int by1, int bx2, int by2) {
        return x >= bx1 &&
                x < bx2 &&
                y >= by1 &&
                y < by2;
    }

    public static void dropInventoryItems(World world, BlockPos pos, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }
}
