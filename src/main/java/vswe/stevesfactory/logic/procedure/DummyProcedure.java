package vswe.stevesfactory.logic.procedure;

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
    public IProcedure[] nexts() {
        return NEXTS_ARR;
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        return null;
    }

    @Override
    public CompoundNBT serialize() {
        return new CompoundNBT();
    }
}
