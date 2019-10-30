package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CapabilitySignalReactor {

    private CapabilitySignalReactor() {
    }

    @CapabilityInject(ISignalReactor.class)
    public static Capability<ISignalReactor> SIGNAL_REACTOR_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISignalReactor.class, new Capability.IStorage<ISignalReactor>() {
            @Override
            public INBT writeNBT(Capability<ISignalReactor> capability, ISignalReactor instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<ISignalReactor> capability, ISignalReactor instance, Direction side, INBT nbt) {
            }
        }, DummySignalReactor::new);
    }

    static class DummySignalReactor implements ISignalReactor {

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
