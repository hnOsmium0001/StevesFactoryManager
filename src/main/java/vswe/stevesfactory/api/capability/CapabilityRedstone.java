package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityRedstone {

    @CapabilityInject(IRedstoneHandler.class)
    public static final Capability<IRedstoneHandler> REDSTONE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IRedstoneHandler.class, new Capability.IStorage<IRedstoneHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IRedstoneHandler> capability, IRedstoneHandler instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IRedstoneHandler> capability, IRedstoneHandler instance, Direction side, INBT nbt) {

            }
        }, RedstoneSignalHandler::new);
    }
}
