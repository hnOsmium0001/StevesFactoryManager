package vswe.stevesfactory.setup;

import net.minecraft.item.*;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public final class ModItems {

    private ModItems() {
    }

    public static final ItemGroup creativeTab = new ItemGroup(StevesFactoryManager.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.factoryManagerBlock);
        }
    };

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        // Register items here
    }

    public static Properties defaultItemProperties() {
        return new Properties().group(creativeTab);
    }
}
