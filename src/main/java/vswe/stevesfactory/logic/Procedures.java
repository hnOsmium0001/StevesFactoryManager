package vswe.stevesfactory.logic;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureFactory;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.procedure.DummyProcedure;
import vswe.stevesfactory.logic.procedure.SimpleProcedureFactory;
import vswe.stevesfactory.utils.RenderingHelper;

import java.util.Arrays;
import java.util.function.Function;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public enum Procedures {
    TRIGGER("trigger", DummyProcedure::new),
    ITEM_IMPORT("item_import", DummyProcedure::new),
    ITEM_EXPORT("item_export", DummyProcedure::new),
    ITEM_CONDITION("item_condition", DummyProcedure::new),
    FLOW_CONTROL("flow_control", DummyProcedure::new),
    FLUID_IMPORT("fluid_import", DummyProcedure::new),
    FLUID_EXPORT("fluid_export", DummyProcedure::new),
    FLUID_CONDITION("fluid_condition", DummyProcedure::new),
    REDSTONE_EMITTER("redstone_emitter", DummyProcedure::new),
    REDSTONE_CONDITION("redstone_condition", DummyProcedure::new),
    CRAFT_ITEM("craft_item", DummyProcedure::new),
    FOR_EACH("for_each", DummyProcedure::new),
    GROUP("group", DummyProcedure::new),
    GROUP_IO("group_io", DummyProcedure::new),
    CAMOUFLAGE("camouflage", DummyProcedure::new),
    SIGN_UPDATER("sign_updater", DummyProcedure::new),
    CONFIGURATIONS("configurations", DummyProcedure::new);

    public final String id;
    public final IProcedureFactory<IProcedure> factory;

    Procedures(String id, Function<INetworkController, IProcedure> constructor) {
        this.id = id;
        this.factory = new SimpleProcedureFactory<>(constructor, RenderingHelper.linkTexture("gui/component_icon", id + ".png"));
        this.factory.setRegistryName(new ResourceLocation(StevesFactoryManager.MODID, id));
    }

    public String getId() {
        return id;
    }

    public ResourceLocation getTexture() {
        return factory.getIcon();
    }

    public IProcedureFactory<?> getFactory() {
        return factory;
    }

    private static final IProcedureFactory<?>[] FACTORIES = Arrays.stream(values())
            .map(Procedures::getFactory)
            .toArray(IProcedureFactory[]::new);

    @SubscribeEvent
    public static void onProcedureRegister(RegistryEvent.Register<IProcedureFactory<?>> event) {
        event.getRegistry().registerAll(Procedures.FACTORIES);
    }
}
