package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;

import javax.annotation.Nullable;

public final class CapabilitySignalReactor {

    private CapabilitySignalReactor() {
    }

    @CapabilityInject(ISignalReactor.class)
    public static Capability<ISignalReactor> SIGNAL_REACTOR_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISignalReactor.class, new Capability.IStorage<ISignalReactor>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ISignalReactor> capability, ISignalReactor instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<ISignalReactor> capability, ISignalReactor instance, Direction side, INBT nbt) {
            }
        }, SignalReactor::new);
    }
}
