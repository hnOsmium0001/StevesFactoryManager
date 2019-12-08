package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;

public final class CapabilityTextDisplay {

    private CapabilityTextDisplay() {
    }

    @CapabilityInject(ITextDisplay.class)
    public static Capability<ITextDisplay> TEXT_DISPLAY_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(ITextDisplay.class, new Capability.IStorage<ITextDisplay>() {
            @Override
            public INBT writeNBT(Capability<ITextDisplay> capability, ITextDisplay instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<ITextDisplay> capability, ITextDisplay instance, Direction side, INBT nbt) {
            }
        }, DynamicTextDisplay::new);
    }
}
