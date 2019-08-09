package vswe.stevesfactory.library.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;

public final class DummyProcedure implements IProcedure {

    private static final ResourceLocation NAME = new ResourceLocation(StevesFactoryManager.MODID, "dummy");
    private static final IProcedure[] NEXTS_ARR = new IProcedure[0];

    public DummyProcedure(INetworkController controller) {
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public IProcedure[] next() {
        return NEXTS_ARR;
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        return null;
    }

    @Override
    public IProcedure[] previous() {
        return NEXTS_ARR;
    }

    @Override
    public void linkTo(int outputIndex, IProcedure next, int nextInputIndex) {
    }

    @Override
    public void unlink(int outputIndex) {
    }

    @Override
    public void onLinkTo(IProcedure previous, int inputIndex) {
    }

    @Override
    public void onUnlink(IProcedure previous) {
    }

    @Override
    public CompoundNBT serialize() {
        return new CompoundNBT();
    }

    public static DummyProcedure deserialize(CompoundNBT tag) {
        return new DummyProcedure(null);
    }
}
