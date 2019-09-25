package vswe.stevesfactory.logic.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.logic.FilterType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface IItemFilter {

    boolean test(ItemStack stack);

    boolean isMatchingAmount();

    void setMatchingAmount(boolean matchingAmount);

    void extractFromInventory(List<ItemStack> target, IItemHandler handler, boolean merge);

    void extractFromInventory(BiConsumer<ItemStack, Integer> receiver, IItemHandler handler);

    FilterType getType();

    void setType(FilterType type);

    void read(CompoundNBT tag);

    void write(CompoundNBT tag);

    default CompoundNBT write() {
        CompoundNBT tag = new CompoundNBT();
        write(tag);
        return tag;
    }

    int limitFlowRate(ItemStack buffered, int existingCount);

    int getTypeID();

    final class ItemFilters {

        private ItemFilters() {
        }

        private static final Int2ObjectMap<Function<CompoundNBT, IItemFilter>> deserializers = new Int2ObjectOpenHashMap<>();
        private static int nextID = 0;

        public static int allocateID(Function<CompoundNBT, IItemFilter> deserializer) {
            int id = nextID++;
            deserializers.put(id, deserializer);
            return id;
        }

        @Nullable
        public static Function<CompoundNBT, IItemFilter> getDeserializerFor(int typeID) {
            return deserializers.get(typeID);
        }
    }
}
