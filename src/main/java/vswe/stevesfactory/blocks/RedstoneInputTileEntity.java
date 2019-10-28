package vswe.stevesfactory.blocks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.*;
import vswe.stevesfactory.setup.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RedstoneInputTileEntity extends BaseTileEntity implements ISignalReactor {

    private List<Predicate<SignalStatus>> eventHandlers = new ArrayList<>();
    private LazyOptional<ISignalReactor> signalReactor = LazyOptional.of(() -> this);

    private SignalStatus lastSignalState;

    public RedstoneInputTileEntity() {
        super(ModBlocks.redstoneInputTileEntity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilitySignalReactor.SIGNAL_REACTOR_CAPABILITY) {
            return signalReactor.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean hasSignal() {
        assert world != null;
        return world.isBlockPowered(pos);
    }

    @Override
    public void subscribeEvent(Consumer<SignalStatus> onChange) {
        eventHandlers.add(status -> {
            onChange.accept(status);
            return false;
        });
    }

    @Override
    public void subscribeEvent(Predicate<SignalStatus> onChange) {
        eventHandlers.add(onChange);
    }

    void onRedstoneChange() {
        assert world != null;
        SignalStatus status = SignalStatus.scan(world, pos);
        if (!lastSignalState.equals(status)) {
            eventHandlers.removeIf(handler -> handler.test(status));
            lastSignalState = status;
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        lastSignalState = new SignalStatus();
        lastSignalState.read(compound.getCompound("LastSignal"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("LastSignal", lastSignalState.write(new CompoundNBT()));
        return super.write(compound);
    }
}
