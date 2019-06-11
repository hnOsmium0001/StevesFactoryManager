package vswe.stevesfactory.setup.builder;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.Objects;
import java.util.function.Function;

public final class TileEntityBuilder<T extends TileEntity> extends RegistryObjectBuilder<TileEntityType<?>, TileEntityType.Builder<T>> {

    private Class<T> tileClass;
    private TileEntityRenderer<? super T> renderer;

    public TileEntityBuilder(String registryName) {
        super(new ResourceLocation(StevesFactoryManager.MODID, registryName));
    }

    public TileEntityBuilder(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public TileEntityBuilder<T> factory(Function<TileEntityType.Builder<T>, TileEntityType<?>> factory) {
        return (TileEntityBuilder<T>) super.factory(factory);
    }

    @Override
    public TileEntityBuilder<T> builder(TileEntityType.Builder<T> builder) {
        return (TileEntityBuilder<T>) super.builder(builder);
    }

    public TileEntityBuilder<T> renderer(Class<T> clazz, TileEntityRenderer<? super T> renderer) {
        this.tileClass = Objects.requireNonNull(clazz);
        this.renderer = Objects.requireNonNull(renderer);
        return this;
    }

    public Class<T> getTileClass() {
        return tileClass;
    }

    public TileEntityRenderer<? super T> getRenderer() {
        return renderer;
    }

    public boolean hasRenderer() {
        return getRenderer() != null;
    }

    public void registerRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(getTileClass(), getRenderer());
    }

    public boolean tryRegisterRenderer() {
        if (hasRenderer()) {
            registerRenderer();
            return true;
        }
        return false;
    }

}
