package vswe.stevesfactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.setup.ModItems;

@Mod(StevesFactoryManager.MODID)
public class StevesFactoryManager {

    public static final String MODID = "sfm";
    public static final String NAME = "Steve's Factory Manager";
    public static final String VERSION = "3.0.0";

    public static final Logger logger = LogManager.getLogger(MODID);

    public static StevesFactoryManager instance;

    public StevesFactoryManager() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        eventBus.addListener(this::serverStarting);
        eventBus.addListener(this::loadComplete);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(this::clientSetup));

        MinecraftForge.EVENT_BUS.register(this);

        ModBlocks.init();
        ModItems.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        instance = (StevesFactoryManager) ModLoadingContext.get().getActiveContainer().getMod();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    private void serverStarting(final FMLServerStartingEvent event) {
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        ModBlocks.finishLoading();
        ModItems.finishLoading();
    }

}
