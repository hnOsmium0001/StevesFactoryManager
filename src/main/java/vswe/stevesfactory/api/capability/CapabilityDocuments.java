package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;

public final class CapabilityDocuments {

    private CapabilityDocuments() {
    }

    @CapabilityInject(ITextDocument.class)
    public static Capability<ITextDocument> TEXT_DISPLAY_CAPABILITY;

    public static void registerTextDocument() {
        CapabilityManager.INSTANCE.register(ITextDocument.class, new Capability.IStorage<ITextDocument>() {
            @Override
            public INBT writeNBT(Capability<ITextDocument> capability, ITextDocument instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<ITextDocument> capability, ITextDocument instance, Direction side, INBT nbt) {
            }
        }, TextDocument::new);
    }
}
