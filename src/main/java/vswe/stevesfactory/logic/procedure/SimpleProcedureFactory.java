package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureFactory;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.blocks.manager.components.FlowComponent;

import java.util.function.Function;

public class SimpleProcedureFactory<P extends IProcedure> extends ForgeRegistryEntry<IProcedureFactory<?>> implements IProcedureFactory<P> {

    private final Function<INetworkController, P> constructor;
    private final ResourceLocation icon;

    public SimpleProcedureFactory(Function<INetworkController, P> constructor, ResourceLocation icon) {
        this.constructor = constructor;
        this.icon = icon;
    }

    @Override
    public P createInstance(INetworkController controller) {
        return constructor.apply(controller);
    }

    @Override
    public P retrieveInstance(CompoundNBT tag) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public FlowComponent createWidget(P procedure) {
        return new FlowComponent(1, 1) {
        };
    }
}