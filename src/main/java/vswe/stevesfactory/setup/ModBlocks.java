package vswe.stevesfactory.setup;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ObjectHolder;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.*;
import vswe.stevesfactory.render.WorkingAreaRenderer;

import java.util.ArrayList;
import java.util.List;

import static vswe.stevesfactory.setup.ModItems.defaultItemProperties;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public final class ModBlocks {

    private ModBlocks() {
    }

    private static List<BlockBuilder> pendingBlocks = new ArrayList<>();

    @ObjectHolder("sfm:factory_manager")
    public static FactoryManagerBlock factoryManagerBlock;
    @ObjectHolder("sfm:factory_manager")
    public static TileEntityType<FactoryManagerTileEntity> factoryManagerTileEntity;

    @ObjectHolder("sfm:cable")
    public static CableBlock cableBlock;
    @ObjectHolder("sfm:cable")
    public static TileEntityType<CableTileEntity> cableTileEntity;

    @ObjectHolder("sfm:redstone_emitter")
    public static RedstoneEmitterBlock redstoneEmitterBlock;
    @ObjectHolder("sfm:redstone_emitter")
    public static TileEntityType<RedstoneEmitterTileEntity> redstoneEmitterTileEntity;

    @ObjectHolder("sfm:redstone_input")
    public static RedstoneInputBlock redstoneInputBlock;
    @ObjectHolder("sfm:redstone_input")
    public static TileEntityType<RedstoneInputTileEntity> redstoneInputTileEntity;

    @ObjectHolder("sfm:item_intake")
    public static ItemIntakeBlock itemIntakeBlock;
    @ObjectHolder("sfm:item_intake")
    public static TileEntityType<ItemIntakeTileEntity> itemIntakeTileEntity;

    @ObjectHolder("sfm:instant_item_intake")
    public static ItemIntakeBlock instantItemIntakeBlock;
    @ObjectHolder("sfm:instant_item_intake")
    public static TileEntityType<ItemIntakeTileEntity> instantItemIntakeTileEntity;

    public static void init() {
        pendingBlocks.add(new BlockBuilder<FactoryManagerTileEntity>("factory_manager")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(4F, 10F))
                .constructor(FactoryManagerBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(FactoryManagerTileEntity::new, block)));

        pendingBlocks.add(new BlockBuilder<CableTileEntity>("cable")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 10F))
                .constructor(CableBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(CableTileEntity::new, block)));

        pendingBlocks.add(new BlockBuilder<RedstoneEmitterTileEntity>("redstone_emitter")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 10F))
                .constructor(RedstoneEmitterBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(RedstoneEmitterTileEntity::new, block)));

        pendingBlocks.add(new BlockBuilder<RedstoneInputTileEntity>("redstone_input")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 10F))
                .constructor(RedstoneInputBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(RedstoneInputTileEntity::new, block)));

        pendingBlocks.add(new BlockBuilder<ItemIntakeTileEntity>("item_intake")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 10F))
                .constructor(props -> new ItemIntakeBlock(ItemIntakeTileEntity::regular, props))
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(ItemIntakeTileEntity::regular, block))
                .forClient(() -> builder -> builder
                        .renderer(ItemIntakeTileEntity.class, WorkingAreaRenderer::new)));

        pendingBlocks.add(new BlockBuilder<ItemIntakeTileEntity>("instant_item_intake")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 10F))
                .constructor(props -> new ItemIntakeBlock(ItemIntakeTileEntity::instant, props))
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(ItemIntakeTileEntity::instant, block))
                .forClient(() -> builder -> builder
                        .renderer(ItemIntakeTileEntity.class, WorkingAreaRenderer::new)));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        pendingBlocks.forEach(b -> event.getRegistry().register(b.construct()));
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        pendingBlocks.forEach(b -> event.getRegistry().register(b.constructItemBlock()));
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        pendingBlocks.forEach(b -> event.getRegistry().register(b.constructTileEntityType()));
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        pendingBlocks.forEach(BlockBuilder::tryRegisterTileEntityRenderer);
    }

    @SubscribeEvent
    public static void finishLoading(FMLLoadCompleteEvent event) {
        pendingBlocks = null;
    }
}
