package vswe.stevesfactory.utils;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

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
}
