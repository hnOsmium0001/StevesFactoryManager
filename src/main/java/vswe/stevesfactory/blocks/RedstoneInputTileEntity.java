package vswe.stevesfactory.blocks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.Config;
import vswe.stevesfactory.api.capability.*;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RedstoneInputTileEntity extends BaseTileEntity implements ICable, ISignalReactor {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") // the .removeIf() call has side effects of triggering the event handlesr
    private List<Predicate<SignalStatus>> eventHandlers = new ArrayList<>();
    private LazyOptional<ISignalReactor> signalReactor = LazyOptional.of(() -> this);

    private SignalStatus lastSignalState = new SignalStatus();

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
        lastSignalState.read(compound.getCompound("LastSignal"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("LastSignal", lastSignalState.write(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean isCable() {
        return Config.COMMON.isRedstoneInputBlockCables.get();
    }

    @Override
    public void addLinksFor(INetworkController controller) {
        NetworkHelper.updateLinksFor(controller, this);
    }
}
