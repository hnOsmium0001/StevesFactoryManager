package vswe.stevesfactory.api;

import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.*;
import vswe.stevesfactory.api.capability.*;
import vswe.stevesfactory.api.logic.IProcedureType;

import java.util.Set;

@EventBusSubscriber(modid = StevesFactoryManagerAPI.MODID, bus = Bus.MOD)
public class StevesFactoryManagerAPI {

    public static final String MODID = "sfm";

    private static IForgeRegistry<IProcedureType<?>> procedures;
    private static Set<Capability<?>> recognizableCapabilities;

    public static IForgeRegistry<IProcedureType<?>> getProceduresRegistry() {
        if (procedures == null) {
            procedures = RegistryManager.ACTIVE.getRegistry(IProcedureType.class);
        }
        return procedures;
    }

    public static Set<Capability<?>> getRecognizableCapabilities() {
        if (recognizableCapabilities == null) {
            recognizableCapabilities = Sets.newIdentityHashSet();
            recognizableCapabilities.add(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            recognizableCapabilities.add(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            recognizableCapabilities.add(CapabilityRedstone.REDSTONE_CAPABILITY);
            recognizableCapabilities.add(CapabilityRedstoneEventBus.REDSTONE_EVENT_BUS_CAPABILITY);
            recognizableCapabilities.add(CapabilityTextDisplay.TEXT_DISPLAY_CAPABILITY);
        }
        return recognizableCapabilities;
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        CapabilityRedstone.register();
        CapabilityRedstoneEventBus.register();
        CapabilityTextDisplay.register();
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegistryCreation(RegistryEvent.NewRegistry event) {
        makeRegistry(new ResourceLocation(MODID, "procedures"), IProcedureType.class).create();
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type) {
        return new RegistryBuilder<T>().setName(name).setType(type);
    }
}
