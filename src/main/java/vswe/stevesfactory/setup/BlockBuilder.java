package vswe.stevesfactory.setup;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vswe.stevesfactory.StevesFactoryManager;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.*;

public final class BlockBuilder<T extends TileEntity> {

    private final ResourceLocation registryName;

    // Block construction
    private Block.Properties blockProperties;
    private Function<Block.Properties, Block> blockConstructor;
    private Block block;

    // Item-form block construction
    private BiFunction<Block, Item.Properties, Item> itemBlockConstructor = BlockItem::new;
    private Item.Properties itemProperties = null;
    private Item item;

    // TileEntityType construction
    private Function<Block, TileEntityType.Builder<T>> tileEntityTypeBuilder = null;
    private Type<T> dataFixerType = null;
    private TileEntityType<T> tileEntityType;

    // TileEntityRenderer construction
    private Class<T> tileClass;
    private Supplier<TileEntityRenderer<T>> rendererFactory;

    public BlockBuilder(String registryName) {
        this(new ResourceLocation(StevesFactoryManager.MODID, registryName));
    }

    public BlockBuilder(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public BlockBuilder<T> properties(@Nonnull Block.Properties blockProperties) {
        this.blockProperties = Objects.requireNonNull(blockProperties);
        return this;
    }

    public BlockBuilder<T> constructor(@Nonnull Function<Block.Properties, Block> blockConstructor) {
        this.blockConstructor = Objects.requireNonNull(blockConstructor);
        return this;
    }

    public BlockBuilder<T> item(@Nonnull Item.Properties itemBuilder, @Nonnull BiFunction<Block, Item.Properties, Item> itemBlockFactory) {
        this.itemProperties = Objects.requireNonNull(itemBuilder);
        this.itemBlockConstructor = Objects.requireNonNull(itemBlockFactory);
        return this;
    }

    public BlockBuilder<T> item(@Nonnull Item.Properties itemBuilder) {
        return item(itemBuilder, itemBlockConstructor);
    }

    public BlockBuilder<T> noItem() {
        itemProperties = null;
        return this;
    }

    public BlockBuilder<T> tileEntity(@Nonnull Function<Block, TileEntityType.Builder<T>> builder) {
        this.tileEntityTypeBuilder = Objects.requireNonNull(builder);
        return this;
    }

    public BlockBuilder<T> dataFixer(@Nonnull Type<T> dataFixerType) {
        this.dataFixerType = Objects.requireNonNull(dataFixerType);
        return this;
    }

    public BlockBuilder<T> noTileEntity() {
        this.tileEntityTypeBuilder = null;
        return this;
    }

    public BlockBuilder<T> renderer(@Nonnull Class<T> tileClass, @Nonnull Supplier<TileEntityRenderer<T>> rendererFactory) {
        this.tileClass = Objects.requireNonNull(tileClass);
        this.rendererFactory = Objects.requireNonNull(rendererFactory);
        return this;
    }

    public Block construct() {
        if (block == null) {
            block = blockConstructor.apply(blockProperties);
            block.setRegistryName(registryName);
        }
        return block;
    }

    public Item constructItemBlock() {
        Preconditions.checkState(hasItem(), "No item properties specified. Unable to construct item-form block!");
        if (item == null) {
            item = itemBlockConstructor.apply(block, itemProperties);
            item.setRegistryName(registryName);
        }
        return item;
    }

    public TileEntityType<T> constructTileEntityType() {
        Preconditions.checkState(hasTileEntity(), "No tile entity builder specified. Unable to construct TileEntityType<T>!");
        if (tileEntityType == null) {
            tileEntityType = tileEntityTypeBuilder.apply(construct()).build(dataFixerType);
            tileEntityType.setRegistryName(registryName);
        }
        return tileEntityType;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean tryRegisterTileEntityRenderer() {
        if (hasTileEntityRenderer()) {
            ClientRegistry.bindTileEntitySpecialRenderer(tileClass, rendererFactory.get());
            return true;
        }
        return false;
    }

    public boolean hasItem() {
        return itemProperties != null;
    }

    public boolean hasTileEntity() {
        return tileEntityTypeBuilder != null;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasTileEntityRenderer() {
        return tileClass != null && rendererFactory != null;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public BlockBuilder<T> forClient(Supplier<Consumer<BlockBuilder<T>>> task) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            task.get().accept(this);
        }
        return this;
    }
}