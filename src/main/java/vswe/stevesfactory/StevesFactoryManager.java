package vswe.stevesfactory;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.setup.ModItems;

@Mod(StevesFactoryManager.MODID)
public class StevesFactoryManager {

    public static final String MODID = "sfm";
    public static final String MOD_NAME = "Steve's Factory Manager";
    public static final String VERSION = "0.0.1";

    public static StevesFactoryManager instance;

    public StevesFactoryManager() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::finishLoading);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(this::setupClient);
        });

        MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.init();
        ModItems.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        instance = (StevesFactoryManager) ModLoadingContext.get().getActiveContainer().getMod();
    }

    private void setupClient(final FMLClientSetupEvent event) {

    }

    private void finishLoading(final FMLLoadCompleteEvent event) {
        ModBlocks.finishLoading();
        ModItems.finishLoading();
    }

}
