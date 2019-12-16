package vswe.stevesfactory.api.visibility;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.api.logic.IProcedureType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public final class GUIVisibility {

    private GUIVisibility() {
    }

    private static final Map<ResourceLocation, BooleanSupplier> map = new HashMap<>();

    public static boolean isEnabled(IProcedureType<?> type) {
        return isEnabled(type.getRegistryName());
    }

    public static boolean isEnabled(ResourceLocation registryName) {
        return map.getOrDefault(registryName, () -> true).getAsBoolean();
    }

    public static void registerEnableState(IProcedureType<?> type, BooleanSupplier getter) {
        Preconditions.checkNotNull(type);
        registerEnableState(type.getRegistryName(), getter);
    }

    public static void registerEnableState(ResourceLocation id, BooleanSupplier getter) {
        Preconditions.checkNotNull(id);
        BooleanSupplier prev = map.putIfAbsent(id, getter);
        Preconditions.checkState(prev == null);
    }
}
