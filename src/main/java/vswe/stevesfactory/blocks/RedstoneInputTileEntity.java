package vswe.stevesfactory.blocks;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.CapabilitySignalReactor;
import vswe.stevesfactory.api.capability.ISignalReactor;
import vswe.stevesfactory.setup.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RedstoneInputTileEntity extends BaseTileEntity implements ISignalReactor {

    private List<BooleanConsumer> eventHandlers = new ArrayList<>();
    private LazyOptional<ISignalReactor> signalReactor = LazyOptional.of(() -> this);

    private boolean lastSignalState;

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

    void onRedstoneChange() {
        boolean signal = hasSignal();
        if (lastSignalState != signal) {
            for (BooleanConsumer eventHandler : eventHandlers) {
                eventHandler.accept(signal);
            }
            lastSignalState = signal;
        }
    }

    @Override
    public void subscribeEvent(BooleanConsumer onChange) {
        eventHandlers.add(onChange);
    }

    @Override
    public void subscribeEvent(Runnable onHigh, Runnable onLow) {
        eventHandlers.add(t -> {
            if (t) {
                onHigh.run();
            } else {
                onLow.run();
            }
        });
    }
}
