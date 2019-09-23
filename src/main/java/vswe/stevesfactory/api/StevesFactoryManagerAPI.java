package vswe.stevesfactory.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.*;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedureType;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public class StevesFactoryManagerAPI {

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
            recognizableCapabilities = new HashSet<>();
            recognizableCapabilities.add(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            recognizableCapabilities.add(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        }
        return recognizableCapabilities;
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegistryCreation(RegistryEvent.NewRegistry event) {
        makeRegistry(new ResourceLocation(StevesFactoryManager.MODID, "procedures"), IProcedureType.class).create();
    }

    // Somehow inlining this method would create an compile error
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type) {
        return new RegistryBuilder<T>().setName(name).setType(type);
    }
}
