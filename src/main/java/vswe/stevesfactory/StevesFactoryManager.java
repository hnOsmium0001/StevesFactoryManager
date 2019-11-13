package vswe.stevesfactory;

import com.mojang.brigadier.arguments.BoolArgumentType;
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
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.network.*;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.setup.ModItems;
import vswe.stevesfactory.ui.manager.selection.ComponentGroup;

@Mod(StevesFactoryManager.MODID)
public class StevesFactoryManager {

    public static final String MODID = "sfm";
    public static final String NAME = "Steve's Factory Manager";

    public static final Logger logger = LogManager.getLogger(MODID);

    public static StevesFactoryManager instance;

    public StevesFactoryManager() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        instance = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(Config::onLoad);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(this::clientSetup));

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

        ModBlocks.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ComponentGroup.reload();
    }

    private void serverStarting(final FMLServerStartingEvent event) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal(MODID)
                .then(settingsCommand())
                .then(reloadCommand());
        event.getCommandDispatcher().register(builder);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands
    ///////////////////////////////////////////////////////////////////////////

    private static LiteralArgumentBuilder<CommandSource> reloadCommand() {
        return Commands.literal("reload")
                .then(componentGroups());
    }

    private static LiteralArgumentBuilder<CommandSource> componentGroups() {
        return Commands
                .literal("componentGroups")
                .requires(source -> source.getEntity() instanceof ServerPlayerEntity)
                .executes(context -> {
                    ServerPlayerEntity client = context.getSource().asPlayer();
                    PacketReloadComponentGroups.reload(client);
                    return 0;
                });
    }

    private static LiteralArgumentBuilder<CommandSource> settingsCommand() {
        return Commands.literal("settings")
                .then(inspectionsOverlay());
    }

    private static LiteralArgumentBuilder<CommandSource> inspectionsOverlay() {
        return Commands
                .literal("inspectionsOverlay")
                // Query setting
                .executes(context -> {
                    ServerPlayerEntity client = context.getSource().asPlayer();
                    PacketSettings.query(client, "inspectionsOverlay");
                    return 0;
                })
                .then(Commands
                        .argument("value", BoolArgumentType.bool())
                        // Set setting
                        .executes(context -> {
                            ServerPlayerEntity client = context.getSource().asPlayer();
                            boolean value = BoolArgumentType.getBool(context, "value");
                            PacketSettings.set(client, "inspectionsOverlay", value);
                            return 0;
                        }));
    }
}
