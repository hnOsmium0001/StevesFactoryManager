package vswe.stevesfactory.setup;

import net.minecraft.item.*;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.setup.builder.RegistryObjectBuilder;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public class ModItems {

    public static final ItemGroup creativeTab = new ItemGroup(StevesFactoryManager.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.factoryManagerBlock);
        }
    };

    private static List<RegistryObjectBuilder<Item, Properties>> pendingItems = new ArrayList<>();

    public static void init() {

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        pendingItems.forEach(b -> event.getRegistry().register(b.construct()));
    }

    public static void finishLoading() {
        pendingItems = null;
    }

    public static Properties defaultItemProperties() {
        return new Properties().group(creativeTab);
    }

}
