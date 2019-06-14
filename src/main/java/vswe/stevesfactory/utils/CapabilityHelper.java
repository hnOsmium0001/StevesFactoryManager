package vswe.stevesfactory.utils;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

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

    public static boolean shouldLink(ICapabilityProvider provider) {
        // TODO registry for capabilities
        return CapabilityHelper.hasCapabilityAtAll(provider, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                CapabilityHelper.hasCapabilityAtAll(provider, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

}
