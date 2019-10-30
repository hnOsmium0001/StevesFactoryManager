package vswe.stevesfactory.blocks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.CapabilityRedstone;
import vswe.stevesfactory.api.capability.RedstoneSignalHandler;
import vswe.stevesfactory.setup.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RedstoneEmitterTileEntity extends BaseTileEntity {

    private LazyOptional<RedstoneSignalHandler> redstoneDown = LazyOptional.of(this::createSignalHandler);
    private LazyOptional<RedstoneSignalHandler> redstoneUp = LazyOptional.of(this::createSignalHandler);
    private LazyOptional<RedstoneSignalHandler> redstoneNorth = LazyOptional.of(this::createSignalHandler);
    private LazyOptional<RedstoneSignalHandler> redstoneSouth = LazyOptional.of(this::createSignalHandler);
    private LazyOptional<RedstoneSignalHandler> redstoneWest = LazyOptional.of(this::createSignalHandler);
    private LazyOptional<RedstoneSignalHandler> redstoneEast = LazyOptional.of(this::createSignalHandler);

    public RedstoneEmitterTileEntity() {
        super(ModBlocks.redstoneEmitterTileEntity);
    }

    private RedstoneSignalHandler createSignalHandler() {
        assert world != null;
        return new RedstoneSignalHandler(() -> {
            world.notifyNeighbors(pos, ModBlocks.redstoneEmitterBlock);
        });
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRedstone.REDSTONE_CAPABILITY) {
            if (side == null) {
                return redstoneUp.cast();
            }
            switch (side) {
                case DOWN: return redstoneDown.cast();
                case UP: return redstoneUp.cast();
                case NORTH: return redstoneNorth.cast();
                case SOUTH: return redstoneSouth.cast();
                case WEST: return redstoneWest.cast();
                case EAST: return redstoneEast.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        redstoneDown.orElseThrow(RuntimeException::new).read(compound.getCompound("Down"));
        redstoneUp.orElseThrow(RuntimeException::new).read(compound.getCompound("Up"));
        redstoneNorth.orElseThrow(RuntimeException::new).read(compound.getCompound("North"));
        redstoneSouth.orElseThrow(RuntimeException::new).read(compound.getCompound("South"));
        redstoneWest.orElseThrow(RuntimeException::new).read(compound.getCompound("West"));
        redstoneEast.orElseThrow(RuntimeException::new).read(compound.getCompound("East"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Down", redstoneDown.orElseThrow(RuntimeException::new).write());
        compound.put("Up", redstoneUp.orElseThrow(RuntimeException::new).write());
        compound.put("North", redstoneNorth.orElseThrow(RuntimeException::new).write());
        compound.put("South", redstoneSouth.orElseThrow(RuntimeException::new).write());
        compound.put("West", redstoneWest.orElseThrow(RuntimeException::new).write());
        compound.put("East", redstoneEast.orElseThrow(RuntimeException::new).write());
        return super.write(compound);
    }
}
