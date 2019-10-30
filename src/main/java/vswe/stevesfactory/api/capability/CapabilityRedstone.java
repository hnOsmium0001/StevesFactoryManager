package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;

public final class CapabilityRedstone {

    private CapabilityRedstone() {
    }

    @CapabilityInject(IRedstoneHandler.class)
    public static Capability<IRedstoneHandler> REDSTONE_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IRedstoneHandler.class, new Capability.IStorage<IRedstoneHandler>() {
            @Override
            public INBT writeNBT(Capability<IRedstoneHandler> capability, IRedstoneHandler instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("Signal", instance.getSignal());
                tag.putBoolean("IsStrong", instance.isStrong());
                return tag;
            }

            @Override
            public void readNBT(Capability<IRedstoneHandler> capability, IRedstoneHandler instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.setSignal(tag.getInt("Signal"));
                instance.setType(IRedstoneHandler.Type.getByIndicator(tag.getBoolean("IsStrong")));
            }
        }, RedstoneSignalHandler::new);
    }
}
