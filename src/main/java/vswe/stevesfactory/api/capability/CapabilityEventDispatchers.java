package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CapabilityEventDispatchers {

    private CapabilityEventDispatchers() {
    }

    @CapabilityInject(IRedstoneEventDispatcher.class)
    public static Capability<IRedstoneEventDispatcher> REDSTONE_EVENT_DISPATCHER_CAPABILITY;

    public static void registerRedstone() {
        CapabilityManager.INSTANCE.register(IRedstoneEventDispatcher.class, new Capability.IStorage<IRedstoneEventDispatcher>() {
            @Override
            public INBT writeNBT(Capability<IRedstoneEventDispatcher> capability, IRedstoneEventDispatcher instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<IRedstoneEventDispatcher> capability, IRedstoneEventDispatcher instance, Direction side, INBT nbt) {
            }
        }, DummyRedstoneEventDispatcher::new);
    }

    static class DummyRedstoneEventDispatcher implements IRedstoneEventDispatcher {

        @Override
        public boolean hasSignal() {
            return false;
        }

        @Override
        public void subscribe(Consumer<SignalStatus> onChange) {
        }

        @Override
        public void subscribe(Predicate<SignalStatus> onChange) {
        }
    }

    @CapabilityInject(IBUDEventDispatcher.class)
    public static Capability<IBUDEventDispatcher> BUD_EVENT_DISPATCHER_CAPABILITY;

    public static void registerBUD() {
        CapabilityManager.INSTANCE.register(IBUDEventDispatcher.class, new Capability.IStorage<IBUDEventDispatcher>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IBUDEventDispatcher> capability, IBUDEventDispatcher instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IBUDEventDispatcher> capability, IBUDEventDispatcher instance, Direction side, INBT nbt) {
            }
        }, DummyBUDEventDispatcher::new);
    }

    static class DummyBUDEventDispatcher implements IBUDEventDispatcher {
        @Override
        public void subscribe(Consumer<BlockPos> handler) {
        }

        @Override
        public void subscribe(Predicate<BlockPos> handler) {
        }
    }
}
