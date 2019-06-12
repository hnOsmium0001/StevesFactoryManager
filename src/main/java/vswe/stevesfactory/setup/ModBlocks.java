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
import vswe.stevesfactory.blocks.cable.CableBlock;
import vswe.stevesfactory.blocks.cable.CableTileEntity;
import vswe.stevesfactory.blocks.manager.FactoryManagerBlock;
import vswe.stevesfactory.blocks.manager.FactoryManagerTileEntity;
import vswe.stevesfactory.setup.builder.BlockBuilder;

import java.util.ArrayList;
import java.util.List;

import static vswe.stevesfactory.setup.ModItems.defaultItemProperties;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public class ModBlocks {

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
        // func_223042_a == create
        pendingBlocks.add(new BlockBuilder<FactoryManagerTileEntity>("factory_manager")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(2f, 10f))
                .constructor(FactoryManagerBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.func_223042_a(FactoryManagerTileEntity::new, block))
                .noRenderer());

        pendingBlocks.add(new BlockBuilder<CableTileEntity>("cable")
                .properties(Block.Properties.create(Material.IRON).hardnessAndResistance(0.4f, 10f))
                .constructor(CableBlock::new)
                .item(defaultItemProperties())
                .tileEntity(block -> TileEntityType.Builder.func_223042_a(CableTileEntity::new, block))
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
