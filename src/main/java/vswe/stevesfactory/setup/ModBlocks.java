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
import net.minecraftforge.registries.ObjectHolder;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.CableBlock;
import vswe.stevesfactory.blocks.CableTileEntity;
import vswe.stevesfactory.blocks.FactoryManagerBlock;
import vswe.stevesfactory.blocks.FactoryManagerTileEntity;
import vswe.stevesfactory.setup.builder.BlockBuilder;

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

    public static void init() {
        pendingBlocks.add(new BlockBuilder<FactoryManagerTileEntity>("factory_manager")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(4F, 10F))
                .constructor(FactoryManagerBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(FactoryManagerTileEntity::new, block))
                .noRenderer());

        pendingBlocks.add(new BlockBuilder<CableTileEntity>("cable")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 10F))
                .constructor(CableBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.create(CableTileEntity::new, block))
                .noRenderer());
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

    public static void finishLoading() {
        pendingBlocks = null;
    }
}
