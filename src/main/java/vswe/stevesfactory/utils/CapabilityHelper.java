package vswe.stevesfactory.utils;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public final class CapabilityHelper {

    private CapabilityHelper() {
    }

    public static boolean hasCapabilityAtAll(ICapabilityProvider provider, Capability<?> cap) {
        for (Direction direction : VectorHelper.DIRECTIONS) {
            if (provider.getCapability(cap, direction).isPresent()) {
                return true;
            }
        }
        return provider.getCapability(cap).isPresent();
    }

}
