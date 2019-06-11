package vswe.stevesfactory.setup;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.manager.FactoryManagerBlock;
import vswe.stevesfactory.setup.builder.BlockBuilder;

import java.util.ArrayList;
import java.util.List;

import static vswe.stevesfactory.setup.ModItems.defaultItemProperties;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public class ModBlocks {

    public static List<BlockBuilder> pendingBlocks = new ArrayList<>();

    @ObjectHolder("sfm:factory_manager")
    public static FactoryManagerBlock factoryManager;

    public static void init() {
        pendingBlocks.add(new BlockBuilder("factory_manager")
                .builder(Block.Properties.create(Material.IRON).hardnessAndResistance(2f, 10f))
                .item(defaultItemProperties())
                .factory(FactoryManagerBlock::new));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        pendingBlocks.forEach(builder -> event.getRegistry().register(builder.construct()));
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        pendingBlocks.forEach(builder -> event.getRegistry().register(builder.constructItemBlock()));
    }

    public static void finishLoading() {
        pendingBlocks = null;
    }

}
