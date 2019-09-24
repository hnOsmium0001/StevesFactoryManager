package vswe.stevesfactory;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.config.ModConfig;

public final class Config {

    private Config() {
    }

    public static final CommonCategory COMMON;

    public static final class CommonCategory {

        // Due to Forge config limitations (and laziness of not wanting to write a custom config)
        // disabled procedures will only be excluded in the selection menu; but kept registered
        public final BooleanValue enableIntervalTrigger;
        public final BooleanValue enableItemTransfer;
        public final BooleanValue enableItemImport;
        public final BooleanValue enableItemExport;

        private CommonCategory(Builder builder) {
            builder.comment("General category").push("common");
            enableIntervalTrigger = builder.define("EnableIntervalTrigger", true);
            enableItemTransfer = builder.define("EnableItemTransfer", true);
            enableItemImport = builder.define("EnableItemImport", true);
            enableItemExport = builder.define("EnableItemExport", true);
            builder.pop();
        }
    }

    public static final ClientCategory CLIENT;

    public static final class ClientCategory {

        public final IntValue scrollSpeed;

        public final IntValue defaultEditorMoveSpeed;
        public final IntValue acceleratedEditorMoveSpeed;
        public final BooleanValue useBackgroundOnFullscreen;

        public final IntValue textButtonBackgroundColor;
        public final IntValue textButtonBorderColor;
        public final IntValue textButtonBackgroundColorHovered;
        public final IntValue textButtonBorderColorHovered;

        private ClientCategory(Builder builder) {
            builder.comment("Client config options").push("client");
            scrollSpeed = builder
                    .comment("How long one move wheel movement for scrolling lists")
                    .defineInRange("ScrollSpeed", 20, 1, 256);
            useBackgroundOnFullscreen = builder
                    .comment("Enable to use vanilla background instead of a plain rectangle")
                    .define("UseBackgroundOnFullscreen", false);
            defaultEditorMoveSpeed = builder
                    .comment("Determines how fast arrow keys moving is (for editor panel)")
                    .defineInRange("DefaultEditorMoveSpeed", 2, 0, Integer.MAX_VALUE);
            acceleratedEditorMoveSpeed = builder
                    .comment("Determines how fast arrow keys moving is, while Shift is pressed")
                    .defineInRange("AcceleratedEditorMoveSpeed", 20, 0, Integer.MAX_VALUE);
            textButtonBackgroundColor = builder.defineInRange("TBBackgroundColor", 0xff8c8c8c, 0, Integer.MAX_VALUE);
            textButtonBorderColor = builder.defineInRange("TBBorderColor", 0xff8c8c8c, 0, Integer.MAX_VALUE);
            textButtonBackgroundColorHovered = builder.defineInRange("TBBackgroundColorHovered", 0xff737373, 0, Integer.MAX_VALUE);
            textButtonBorderColorHovered = builder.defineInRange("TBBorderColorHovered", 0xffc9c9c9, 0, Integer.MAX_VALUE);
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
