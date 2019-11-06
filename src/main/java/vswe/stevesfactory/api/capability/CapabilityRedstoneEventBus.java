package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CapabilityRedstoneEventBus {

    private CapabilityRedstoneEventBus() {
    }

    @CapabilityInject(IRedstoneEventBus.class)
    public static Capability<IRedstoneEventBus> REDSTONE_EVENT_BUS_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IRedstoneEventBus.class, new Capability.IStorage<IRedstoneEventBus>() {
            @Override
            public INBT writeNBT(Capability<IRedstoneEventBus> capability, IRedstoneEventBus instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<IRedstoneEventBus> capability, IRedstoneEventBus instance, Direction side, INBT nbt) {
            }
        }, DummyRedstoneEventBus::new);
    }

    static class DummyRedstoneEventBus implements IRedstoneEventBus {

        @Override
        public boolean hasSignal() {
            return false;
        }

        @Override
        public void subscribeEvent(Consumer<SignalStatus> onChange) {
        }

        @Override
        public void subscribeEvent(Predicate<SignalStatus> onChange) {
        }
    }
}
