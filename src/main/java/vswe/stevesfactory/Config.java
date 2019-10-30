package vswe.stevesfactory;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;

public final class Config {

    private Config() {
    }

    public static final CommonCategory COMMON;

    public static final class CommonCategory {

        public final IntValue maxSearchDepth;
        public final IntValue rescanInterval;

        public final BooleanValue isRedstoneInputBlockCables;
        public final BooleanValue isRedstoneEmitterBlockCables;

        // Due to Forge config limitations (and laziness of not wanting to write a custom config)
        // disabled procedures will only be excluded in the selection menu; but kept registered
        public final BooleanValue enableIntervalTrigger;
        public final BooleanValue enableRedstoneTrigger;
        public final BooleanValue enableItemTransfer;
        public final BooleanValue enableItemImport;
        public final BooleanValue enableItemExport;
        public final BooleanValue enableCrafting;
        public final BooleanValue enableRedstoneEmitter;

        private CommonCategory(Builder builder) {
            builder.comment("Factory manager config options").push("factoryManager");
            maxSearchDepth = builder
                    .comment("Maximum depth that the Factory Manager DFS algorithm should go")
                    .defineInRange("MaxSearchDepth", 64, 0, Integer.MAX_VALUE);
            rescanInterval = builder
                    .comment("Number of ticks for the Factory Manager to rescan the network. Set to -1 to make it never rescan passively")
                    .defineInRange("RescanInterval", 100, -1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Block property config options").push("blocks");
            isRedstoneInputBlockCables = builder
                    .comment("Whether the redstone input block is considered a cable")
                    .define("IsRedstoneInputBlockCables", false);
            isRedstoneEmitterBlockCables = builder
                    .comment("Whether the redstone emitter block is considered a cable")
                    .define("IsRedstoneEmitterBlockCables", false);
            builder.pop();

            builder.comment("Procedures config options", "Run '/sfm reload componentGroups' after updating config").push("procedures");
            enableIntervalTrigger = builder.define("EnableIntervalTrigger", true);
            enableRedstoneTrigger = builder.define("EnableRedstoneTrigger", true);
            enableItemTransfer = builder.define("EnableItemTransfer", true);
            enableItemImport = builder.define("EnableItemImport", true);
            enableItemExport = builder.define("EnableItemExport", true);
            enableCrafting = builder.define("EnableCrafting", true);
            enableRedstoneEmitter = builder.define("EnableRedstoneEmitter", true);
            builder.pop();
        }
    }

    public static final ClientCategory CLIENT;

    public static final class ClientCategory {

        public final IntValue scrollSpeed;
        public final BooleanValue enableInspections;

        public final BooleanValue useBackgroundOnFullscreen;
        public final IntValue defaultEditorMoveSpeed;
        public final IntValue acceleratedEditorMoveSpeed;

        private ClientCategory(Builder builder) {
            builder.comment("Client config options").push("client");
            scrollSpeed = builder
                    .comment("How long one move wheel movement for scrolling lists")
                    .defineInRange("ScrollSpeed", 20, 1, 256);
            enableInspections = builder
                    .comment("Default value for InspectionBoxHighlighting (modifiable in-game via command /sfm settings, but does not persist)")
                    .define("EnableInspections", false);
            useBackgroundOnFullscreen = builder
                    .comment("Enable to use vanilla background instead of a plain rectangle")
                    .define("UseBackgroundOnFullscreen", false);
            defaultEditorMoveSpeed = builder
                    .comment("Determines how fast arrow keys moving is (for editor panel)")
                    .defineInRange("DefaultEditorMoveSpeed", 2, 0, Integer.MAX_VALUE);
            acceleratedEditorMoveSpeed = builder
                    .comment("Determines how fast arrow keys moving is, while Shift is pressed")
                    .defineInRange("AcceleratedEditorMoveSpeed", 20, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    static final ForgeConfigSpec COMMON_SPEC;

    static {
        Builder builder = new Builder();
        COMMON = new CommonCategory(builder);
        COMMON_SPEC = builder.build();
    }

    static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Builder builder = new Builder();
        CLIENT = new ClientCategory(builder);
        CLIENT_SPEC = builder.build();
    }

    static void onLoad(ModConfig.Loading event) {
        StevesFactoryManager.logger.debug("Loaded {} config file {}", StevesFactoryManager.MODID, event.getConfig().getFileName());
    }
}
