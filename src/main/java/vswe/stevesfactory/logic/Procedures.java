package vswe.stevesfactory.logic;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.Config;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.blocks.BUDBlock;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.logic.procedure.*;
import vswe.stevesfactory.utils.NetworkHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, bus = Bus.MOD)
public final class Procedures<P extends IProcedure> {

    public static final Procedures<IntervalTriggerProcedure> INTERVAL_TRIGGER = new Procedures<>("interval_trigger", IntervalTriggerProcedure::new, Config.COMMON.enableIntervalTrigger);
    public static final Procedures<RedstoneTriggerProcedure> REDSTONE_TRIGGER = new Procedures<>("redstone_trigger", RedstoneTriggerProcedure::new, Config.COMMON.enableRedstoneTrigger);
    public static final Procedures<BUDTriggerProcedure> BUD_TRIGGER = new Procedures<>("bud_trigger", BUDTriggerProcedure::new, Config.COMMON.enableBUDTrigger);
    public static final Procedures<ItemTransferProcedure> ITEM_TRANSFER = new Procedures<>("item_transfer", ItemTransferProcedure::new, Config.COMMON.enableItemTransfer);
    public static final Procedures<ItemImportProcedure> ITEM_IMPORT = new Procedures<>("item_import", ItemImportProcedure::new, Config.COMMON.enableItemImport);
    public static final Procedures<ItemExportProcedure> ITEM_EXPORT = new Procedures<>("item_export", ItemExportProcedure::new, Config.COMMON.enableItemExport);
    public static final Procedures<CraftingProcedure> CRAFTING = new Procedures<>("crafting", CraftingProcedure::new, Config.COMMON.enableItemExport);
    public static final Procedures<FluidTransferProcedure> FLUID_TRANSFER = new Procedures<>("fluid_transfer", FluidTransferProcedure::new, Config.COMMON.enableFluidTransfer);
    public static final Procedures<RedstoneEmitterProcedure> REDSTONE_EMITTER = new Procedures<>("redstone_emitter", RedstoneEmitterProcedure::new, Config.COMMON.enableRedstoneEmitter);
    public static final Procedures<SignUpdaterProcedure> SIGN_UPDATER = new Procedures<>("sign_updater", SignUpdaterProcedure::new, Config.COMMON.enableSignUpdater);

    public static final Map<ResourceLocation, Procedures<?>> map = new HashMap<>();

    static {
        map.put(INTERVAL_TRIGGER.getRegistryName(), INTERVAL_TRIGGER);
        map.put(REDSTONE_TRIGGER.getRegistryName(), REDSTONE_TRIGGER);
        map.put(BUD_TRIGGER.getRegistryName(), BUD_TRIGGER);
        map.put(ITEM_TRANSFER.getRegistryName(), ITEM_TRANSFER);
        map.put(ITEM_IMPORT.getRegistryName(), ITEM_IMPORT);
        map.put(ITEM_EXPORT.getRegistryName(), ITEM_EXPORT);
        map.put(CRAFTING.getRegistryName(), CRAFTING);
        map.put(FLUID_TRANSFER.getRegistryName(), FLUID_TRANSFER);
        map.put(REDSTONE_EMITTER.getRegistryName(), REDSTONE_EMITTER);
        map.put(SIGN_UPDATER.getRegistryName(), SIGN_UPDATER);
    }

    public final String id;
    public final SimpleProcedureType<P> factory;
    public final ForgeConfigSpec.BooleanValue enabled;

    private Procedures(String id, Supplier<P> rawConstructor, ForgeConfigSpec.BooleanValue enabled) {
        this.id = id;
        this.factory = new SimpleProcedureType<>(NetworkHelper.wrapConstructor(rawConstructor), rawConstructor, RenderingHelper.linkTexture("gui/component_icon", id + ".png"));
        this.factory.setRegistryName(new ResourceLocation(StevesFactoryManager.MODID, id));
        this.enabled = enabled;
    }

    public String getPathComponent() {
        return id;
    }

    public ResourceLocation getTexture() {
        return factory.getIcon();
    }

    public ResourceLocation getRegistryName() {
        return factory.getRegistryName();
    }

    public IProcedureType<?> getFactory() {
        return factory;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    @SubscribeEvent
    public static void onProcedureRegister(RegistryEvent.Register<IProcedureType<?>> event) {
        for (Procedures<?> procedure : map.values()) {
            event.getRegistry().register(procedure.getFactory());
        }
    }

    public static boolean isEnabled(IProcedureType<?> type) {
        return isEnabled(type.getRegistryName());
    }

    public static boolean isEnabled(ResourceLocation registryName) {
        return map.get(registryName).isEnabled();
    }
}
