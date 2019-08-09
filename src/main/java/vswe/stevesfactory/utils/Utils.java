package vswe.stevesfactory.utils;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Arrays;

/**
 * Collection of general helper methods that doesn't worth creating an extra helper class for them.
 */
public final class Utils {

    private Utils() {
    }

    public static boolean hasCapabilityAtAll(ICapabilityProvider provider, Capability<?> cap) {
        for (Direction direction : VectorHelper.DIRECTIONS) {
            if (provider.getCapability(cap, direction).isPresent()) {
                return true;
            }
        }
        return provider.getCapability(cap).isPresent();
    }

    /**
     * Semantic purposing method that directs to {@link Math#max(int, int)}.
     */
    public static int lowerBound(int i, int lowerBound) {
        return Math.max(i, lowerBound);
    }

    /**
     * Semantic purposing method that directs to {@link Math#min(int, int)}.
     */
    public static int upperBound(int i, int upperBound) {
        return Math.min(i, upperBound);
    }

    /**
     * Create an {@code end-start} long int array, where the first element is {@code start}, and each element after is 1 bigger than the previous element.
     */
    public static int[] rangedIntArray(int start, int end) {
        int[] result = new int[end - start];
        Arrays.setAll(result, i -> i + start);
        return result;
    }
}
