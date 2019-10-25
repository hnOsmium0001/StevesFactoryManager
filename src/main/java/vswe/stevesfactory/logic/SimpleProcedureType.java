package vswe.stevesfactory.logic;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nonnull;
import java.util.function.*;

public class SimpleProcedureType<P extends IProcedure> extends ForgeRegistryEntry<IProcedureType<?>> implements IProcedureType<P> {

    private final Function<INetworkController, P> constructor;
    private final Supplier<P> rawConstructor;
    private final ResourceLocation icon;
    private String translationKey = null;

    public SimpleProcedureType(BiFunction<IProcedureType<P>, INetworkController, P> constructor, Function<IProcedureType<P>, P> rawConstructor, ResourceLocation icon) {
        this.constructor = c -> constructor.apply(this, c);
        this.rawConstructor = () -> rawConstructor.apply(this);
        this.icon = icon;
    }

    public SimpleProcedureType(Function<INetworkController, P> constructor, Supplier<P> rawConstructor, ResourceLocation icon) {
        this.constructor = constructor;
        this.rawConstructor = rawConstructor;
        this.icon = icon;
    }

    @Override
    public P createInstance(INetworkController controller) {
        return constructor.apply(controller);
    }

    @Override
    public P retrieveInstance(CompoundNBT tag) {
        P procedure = rawConstructor.get();
        procedure.deserialize(tag);
        return procedure;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        if (translationKey == null) {
            translationKey = "logic." + getRegistryNameNonnull().toString().replace(':', '.');
        }
        return translationKey;
    }

    @Nonnull
    public ResourceLocation getRegistryNameNonnull() {
        ResourceLocation id = super.getRegistryName();
        Preconditions.checkState(id != null, "Procedure type " + this + " does not have a registry name!");
        return id;
    }
}