package vswe.stevesfactory.blocks;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.*;
import vswe.stevesfactory.setup.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RedstoneEmitterTileEntity extends BaseTileEntity {

    // TODO one for each side
    private LazyOptional<IRedstoneHandler> redstoneCap = LazyOptional.of(RedstoneSignalHandler::new);

    public RedstoneEmitterTileEntity() {
        super(ModBlocks.redstoneEmitterTileEntity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRedstone.REDSTONE_CAPABILITY) {
            return redstoneCap.cast();
        }
        return super.getCapability(cap, side);
    }
}
