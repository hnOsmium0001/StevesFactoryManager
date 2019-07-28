package vswe.stevesfactory.api;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.*;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedureFactory;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public class SFMAPI {

    private static IForgeRegistry<IProcedureFactory<?>> procedures;

    public static IForgeRegistry<IProcedureFactory<?>> getProceduresRegistry() {
        Preconditions.checkNotNull(procedures, "Trying to access the registry sfm:procedures before it was created");
        return procedures;
    }

    @SubscribeEvent
    public static void onRegistryCreation(RegistryEvent.NewRegistry event) {
        procedures = new RegistryBuilder<IProcedureFactory<?>>()
                .setName(new ResourceLocation(StevesFactoryManager.MODID, "procedures"))
                .create();
    }

}
