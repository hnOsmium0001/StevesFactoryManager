package vswe.stevesfactory.setup.builder;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Objects;
import java.util.function.Function;

public class RegistryObjectBuilder<T extends IForgeRegistryEntry<T>, B> {

    private final ResourceLocation registryName;
    private Function<B, T> factory;
    private B builder;

    private T product;

    public RegistryObjectBuilder(String registryName) {
        this(new ResourceLocation(Objects.requireNonNull(registryName)));
    }

    public RegistryObjectBuilder(ResourceLocation registryName) {
        this.registryName = Objects.requireNonNull(registryName);
    }

    public RegistryObjectBuilder<T, B> factory(Function<B, T> factory) {
        this.factory = Objects.requireNonNull(factory);
        return this;
    }

    public RegistryObjectBuilder<T, B> builder(B builder) {
        this.builder = Objects.requireNonNull(builder);
        return this;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public T construct() {
        if (product == null) {
            Preconditions.checkState(factory != null, "Cannot construct %s without a Factory function!", getRegistryName());
            Preconditions.checkState(builder != null, "Cannot construct %s without a Builder!", getRegistryName());
            product = factory.apply(builder).setRegistryName(getRegistryName());
        }
        return product;
    }

}
