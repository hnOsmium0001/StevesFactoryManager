package vswe.stevesfactory.logic.procedure;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;
import java.util.List;

public final class DummyProcedure implements IProcedure {

    private static final ResourceLocation NAME = new ResourceLocation(StevesFactoryManager.MODID, "dummy");

    public DummyProcedure(INetworkController controller) {
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public List<? extends IProcedure> nexts() {
        return ImmutableList.of();
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        return null;
    }
}
