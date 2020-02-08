package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.Config;
import vswe.stevesfactory.api.capability.CapabilityEventDispatchers;
import vswe.stevesfactory.api.capability.IBUDEventDispatcher;
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

public class BUDTileEntity extends TileEntity implements ICable, IBUDEventDispatcher {

    private List<Predicate<BlockPos>> handlers = new ArrayList<>();
    private LazyOptional<IBUDEventDispatcher> capability = LazyOptional.of(() -> this);

    public BUDTileEntity() {
        super(ModBlocks.budTileEntity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEventDispatchers.BUD_EVENT_DISPATCHER_CAPABILITY) {
            return capability.cast();
        }
        return super.getCapability(cap, side);
    }

    public void onNeighborChanged(BlockPos fromPos) {
        handlers.removeIf(handler -> handler.test(fromPos));
    }

    @Override
    public void subscribe(Consumer<BlockPos> handler) {
        subscribe(event -> {
            handler.accept(event);
            return false;
        });
    }

    @Override
    public void subscribe(Predicate<BlockPos> handler) {
        handlers.add(handler);
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean isCable() {
        return Config.COMMON.isBUDBlockCables.get();
    }

    @Override
    public void addLinksFor(INetworkController controller) {
        NetworkHelper.updateLinksFor(controller, this);
    }
}
