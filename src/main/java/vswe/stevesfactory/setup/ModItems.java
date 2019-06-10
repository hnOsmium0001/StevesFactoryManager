package vswe.stevesfactory.setup;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.setup.builder.RegistryObjectBuilder;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public class ModItems {

    public static ItemGroup creativeTab = new ItemGroup(StevesFactoryManager.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.factoryManager);
        }
    };

    private static List<RegistryObjectBuilder<Item, Properties>> pendingItems = new ArrayList<>();

    public static void init() {

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        pendingItems.forEach(builder -> event.getRegistry().register(builder.construct()));
    }

    public static void finishLoading() {
        pendingItems = null;
    }

    public static Properties defaultItemProperties() {
        return new Properties().group(creativeTab);
    }

}
