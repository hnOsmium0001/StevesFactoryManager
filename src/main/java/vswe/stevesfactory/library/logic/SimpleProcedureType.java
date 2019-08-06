package vswe.stevesfactory.library.logic;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.ui.manager.components.FlowComponent;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class SimpleProcedureType<P extends IProcedure> extends ForgeRegistryEntry<IProcedureType<?>> implements IProcedureType<P> {

    private final Function<INetworkController, P> constructor;
    private final ResourceLocation icon;

    private final Function<CompoundNBT, P> retriever;

    public SimpleProcedureType(Function<INetworkController, P> constructor, Function<CompoundNBT, P> retriever, ResourceLocation icon) {
        this.constructor = constructor;
        this.icon = icon;
        this.retriever = retriever;
    }

    @Override
    public P createInstance(INetworkController controller) {
        return constructor.apply(controller);
    }

    @Override
    public P retrieveInstance(CompoundNBT tag) {
        Preconditions.checkArgument(getRegistryNameNonnull().toString().equals(tag.getString("ID")));
        return retriever.apply(tag);
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

    @Nonnull
    public ResourceLocation getRegistryNameNonnull() {
        ResourceLocation id = super.getRegistryName();
        Preconditions.checkState(id != null, "Procedure type " + this + " does not have a registry name!");
        return id;
    }
}