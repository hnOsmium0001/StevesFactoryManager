package vswe.stevesfactory;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;

public final class Config {

    private Config() {
    }

    public static final CommonCategory COMMON;

    public static final class CommonCategory {

        // Factory manager options
        public final IntValue maxSearchDepth;
        public final IntValue rescanInterval;

        // Item intake options
        public final IntValue regularPickupInterval;
        public final IntValue instantPickupInterval;
        public final IntValue regularMaxRadius;
        public final IntValue instantMaxRadius;

        // Block property options
        public final BooleanValue isRedstoneInputBlockCables;
        public final BooleanValue isRedstoneEmitterBlockCables;
        public final BooleanValue isItemIntakeBlockCables;
        public final BooleanValue isInstantItemIntakeBlockCables;

        // Procedure options
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
                    .defineInRange("maxSearchDepth", 64, 0, Integer.MAX_VALUE);
            rescanInterval = builder
                    .comment("Number of ticks for the Factory Manager to rescan the network. Set to -1 to make it never rescan passively")
                    .defineInRange("rescanInterval", 100, -1, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Item intake config options").push("itemIntake");
            regularPickupInterval = builder
                    .comment("Interval between each pickup attempt for regular item intake, in ticks")
                    .defineInRange("regularPickupInterval", 20, 0, Integer.MAX_VALUE);
            instantPickupInterval = builder
                    .comment("Interval between each pickup attempt for instant item intake, in ticks")
                    .defineInRange("instantPickupInterval", 20, 0, Integer.MAX_VALUE);
            regularMaxRadius = builder
                    .comment("Maximum pickup radius that an item take can have, for regular item intake")
                    .defineInRange("regularMaxPickupDistance", 3, 0, Integer.MAX_VALUE);
            instantMaxRadius = builder
                    .comment("Maximum pickup radius that an item take can have, for instant item intake")
                    .defineInRange("instantMaxPickupDistance", 3, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("Block property config options").push("blocks");
            isRedstoneInputBlockCables = builder
                    .comment("Whether the redstone input block (sfm:redstone_input) is considered a cable")
                    .define("isRedstoneInputBlockCables", false);
            isRedstoneEmitterBlockCables = builder
                    .comment("Whether the redstone emitter block (sfm:redstone_emitter) is considered a cable")
                    .define("isRedstoneEmitterBlockCables", false);
            isItemIntakeBlockCables = builder
                    .comment("Whether the item intake block (sfm:item_intake) is considered a cable")
                    .define("isItemIntakeBlockCables", false);
            isInstantItemIntakeBlockCables = builder
                    .comment("Whether the instant item intake block (sfm:instant_item_intake) is considered a cable")
                    .define("isInstantItemIntakeBlockCables", false);
            builder.pop();

            builder.comment("Procedures config options", "Run '/sfm reload componentGroups' after updating config").push("procedures");
            enableIntervalTrigger = builder.define("enableIntervalTrigger", true);
            enableRedstoneTrigger = builder.define("enableRedstoneTrigger", true);
            enableItemTransfer = builder.define("enableItemTransfer", true);
            enableItemImport = builder.define("enableItemImport", true);
            enableItemExport = builder.define("enableItemExport", true);
            enableCrafting = builder.define("enableCrafting", true);
            enableRedstoneEmitter = builder.define("enableRedstoneEmitter", true);
            builder.pop();
        }
    }

    public static final ClientCategory CLIENT;

    public static final class ClientCategory {

        // Widgets options
        public final IntValue scrollSpeed;
        public final BooleanValue enableInspections;

        // Factory manager GUI options
        public final BooleanValue useBackgroundOnFullscreen;
        public final IntValue defaultEditorMoveSpeed;
        public final IntValue acceleratedEditorMoveSpeed;

        private ClientCategory(Builder builder) {
            builder.comment("Client config options").push("generalGUI");
            scrollSpeed = builder
                    .comment("How long one move wheel movement for scrolling lists")
                    .defineInRange("scrollSpeed", 20, 1, 256);
            enableInspections = builder
                    .comment("Default value for InspectionBoxHighlighting", "Modifiable in-game via command /sfm settings, but does not persist through relaunches")
                    .define("enableInspections", false);
            builder.pop();

            builder.comment("Factory manager GUI config options").push("factoryManagerGUI");
            useBackgroundOnFullscreen = builder
                    .comment("Enable to use vanilla background instead of a plain rectangle")
                    .define("useBackgroundOnFullscreen", false);
            defaultEditorMoveSpeed = builder
                    .comment("Determines how fast arrow keys moving is (for editor panel)")
                    .defineInRange("defaultEditorMoveSpeed", 2, 0, Integer.MAX_VALUE);
            acceleratedEditorMoveSpeed = builder
                    .comment("Determines how fast arrow keys moving is, while Shift is pressed")
                    .defineInRange("acceleratedEditorMoveSpeed", 20, 0, Integer.MAX_VALUE);
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
