package vswe.stevesfactory.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.logic.SimpleProcedureType;
import vswe.stevesfactory.logic.procedure.*;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.utils.RenderingHelper;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public final class Procedures<P extends IProcedure> {

    public static final Procedures<TimedTriggerProcedure> TRIGGER = new Procedures<>(
            "trigger",
            TimedTriggerProcedure::new,
            TimedTriggerProcedure::deserialize,
            TimedTriggerProcedure::createFlowComponent);

    public static final Procedures<SingletonItemTransferProcedure> SINGLETON_ITEM_TRANSFER = new Procedures<>(
            "singleton_item_transfer",
            SingletonItemTransferProcedure::new,
            SingletonItemTransferProcedure::deserialize,
            SingletonItemTransferProcedure::createFlowComponent);

    public static final Procedures<BatchedItemTransferProcedure> BATCHED_ITEM_TRANSFER = new Procedures<>(
            "batched_item_transfer",
            BatchedItemTransferProcedure::new,
            BatchedItemTransferProcedure::deserialize,
            BatchedItemTransferProcedure::createFlowComponent);

//    ITEM_IMPORT("item_import", DummyProcedure::new),
//    ITEM_EXPORT("item_export", DummyProcedure::new),
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

    private Procedures(String id, Function<INetworkController, P> constructor, Function<CompoundNBT, P> retriever) {
        this(id, constructor, retriever, null);
    }

    private Procedures(String id, Function<INetworkController, P> constructor, Function<CompoundNBT, P> retriever, Function<P, FlowComponent> flowComponentFactory) {
        this.id = id;
        this.factory = new SimpleProcedureType<P>(constructor, retriever, RenderingHelper.linkTexture("gui/component_icon", id + ".png"));
        this.factory.setRegistryName(new ResourceLocation(StevesFactoryManager.MODID, id));
        this.factory.setFlowComponentFactory(flowComponentFactory);
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
