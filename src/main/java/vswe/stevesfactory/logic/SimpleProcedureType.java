package vswe.stevesfactory.logic;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleProcedureType<P extends IProcedure> extends ForgeRegistryEntry<IProcedureType<?>> implements IProcedureType<P> {

    private final Function<INetworkController, P> constructor;
    private final ResourceLocation icon;

    public SimpleProcedureType(BiFunction<IProcedureType<P>, INetworkController, P> constructor, ResourceLocation icon) {
        this.constructor = c -> constructor.apply(this, c);
        this.icon = icon;
    }

    public SimpleProcedureType(Function<INetworkController, P> constructor, ResourceLocation icon) {
        this.constructor = constructor;
        this.icon = icon;
    }

    @Override
    public P createInstance(INetworkController controller) {
        return constructor.apply(controller);
    }

    @Override
    public P retrieveInstance(CommandGraph graph, CompoundNBT tag) {
        Preconditions.checkArgument(getRegistryNameNonnull().toString().equals(tag.getString("ID")));
        P procedure = createInstance(graph.getController());
        procedure.deserialize(graph, tag);
        return procedure;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Nonnull
    public ResourceLocation getRegistryNameNonnull() {
        ResourceLocation id = super.getRegistryName();
        Preconditions.checkState(id != null, "Procedure type " + this + " does not have a registry name!");
        return id;
    }
}