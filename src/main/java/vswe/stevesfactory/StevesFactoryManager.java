package vswe.stevesfactory;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.network.NetworkHandler;
import vswe.stevesfactory.network.PacketReloadComponentGroups;
import vswe.stevesfactory.setup.ModContainers;
import vswe.stevesfactory.ui.manager.selection.ComponentGroup;

@Mod(StevesFactoryManager.MODID)
public class StevesFactoryManager {

    public static final String MODID = "sfm";
    public static final String NAME = "Steve's Factory Manager";

    public static final Logger logger = LogManager.getLogger(MODID);

    public static StevesFactoryManager instance;

    public StevesFactoryManager() {
        instance = this;
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(Config::onLoad);
        eventBus.addListener(Config::onConfigChanged);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(this::clientSetup));

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onPlayerLoggedIn);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModContainers.registerFactories();
        ComponentGroup.reload(false);
    }

    private void serverStarting(final FMLServerStartingEvent event) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal(MODID)
                .then(componentGroupsCommand());
        event.getCommandDispatcher().register(builder);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands
    ///////////////////////////////////////////////////////////////////////////

    private static LiteralArgumentBuilder<CommandSource> componentGroupsCommand() {
        return Commands.literal("componentGroups")
                .then(Commands.literal("reload")
                        .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                        .executes(context -> {
                            ServerPlayerEntity client = context.getSource().asPlayer();
                            PacketReloadComponentGroups.reload(client);
                            return 0;
                        }))
                .then(Commands.literal("reset")
                        .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                        .executes(context -> {
                            ServerPlayerEntity client = context.getSource().asPlayer();
                            PacketReloadComponentGroups.reset(client);
                            return 0;
                        }));
    }
}
