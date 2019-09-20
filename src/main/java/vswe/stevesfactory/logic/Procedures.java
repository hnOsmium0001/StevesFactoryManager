package vswe.stevesfactory.logic;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.procedure.BatchedItemTransferProcedure;
import vswe.stevesfactory.logic.procedure.ItemExportProcedure;
import vswe.stevesfactory.logic.procedure.ItemImportProcedure;
import vswe.stevesfactory.logic.procedure.TimedTriggerProcedure;
import vswe.stevesfactory.utils.RenderingHelper;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public final class Procedures<P extends IProcedure> {

    public static final Procedures<TimedTriggerProcedure> TIMED_TRIGGER = new Procedures<>("timed_trigger", TimedTriggerProcedure::new, TimedTriggerProcedure::new);
    public static final Procedures<BatchedItemTransferProcedure> BATCHED_ITEM_TRANSFER = new Procedures<>("batched_item_transfer", BatchedItemTransferProcedure::new, BatchedItemTransferProcedure::new);
    public static final Procedures<ItemImportProcedure> ITEM_IMPORT = new Procedures<>("item_import", ItemImportProcedure::new, ItemImportProcedure::new);
    public static final Procedures<ItemExportProcedure> ITEM_EXPORT = new Procedures<>("item_export", ItemExportProcedure::new, ItemExportProcedure::new);

//    ITEM_CONDITION("item_condition", DummyProcedure::new),
//    FLOW_CONTROL("flow_control", DummyProcedure::new),
//    FLUID_IMPORT("fluid_import", DummyProcedure::new),
//    FLUID_EXPORT("fluid_export", DummyProcedure::new),
//    FLUID_CONDITION("fluid_condition", DummyProcedure::new),
//    REDSTONE_EMITTER("redstone_emitter", DummyProcedure::new),
//    REDSTONE_CONDITION("redstone_condition", DummyProcedure::new),
//    CRAFT_ITEM("craft_item", DummyProcedure::new),
//    FOR_EACH("for_each", DummyProcedure::new),
//    GROUP("group", DummyProcedure::new),
//    GROUP_IO("group_io", DummyProcedure::new),
//    CAMOUFLAGE("camouflage", DummyProcedure::new),
//    SIGN_UPDATER("sign_updater", DummyProcedure::new),
//    CONFIGURATIONS("configurations", DummyProcedure::new),

    public final String id;
    public final SimpleProcedureType<P> factory;

    private Procedures(String id, Function<INetworkController, P> constructor, Function<CommandGraph, P> retriever) {
        this.id = id;
        this.factory = new SimpleProcedureType<>(constructor, retriever, RenderingHelper.linkTexture("gui/component_icon", id + ".png"));
        this.factory.setRegistryName(new ResourceLocation(StevesFactoryManager.MODID, id));
    }

    public String getPathComponent() {
        return id;
    }

    public ResourceLocation getTexture() {
        return factory.getIcon();
    }

    public IProcedureType<?> getFactory() {
        return factory;
    }

    private static final IProcedureType<?>[] FACTORIES = Arrays.stream(Procedures.class.getDeclaredFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()))
            .filter(f -> f.getType() == Procedures.class)
            .map(f -> {
                try {
                    return (Procedures<?>) f.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(Procedures::getFactory)
            .toArray(IProcedureType[]::new);

    @SubscribeEvent
    public static void onProcedureRegister(RegistryEvent.Register<IProcedureType<?>> event) {
        event.getRegistry().registerAll(Procedures.FACTORIES);
    }
}
