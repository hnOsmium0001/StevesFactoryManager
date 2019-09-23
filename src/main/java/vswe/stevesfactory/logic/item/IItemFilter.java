package vswe.stevesfactory.logic.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.logic.FilterType;

import java.util.List;
import java.util.function.BiConsumer;

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
}
