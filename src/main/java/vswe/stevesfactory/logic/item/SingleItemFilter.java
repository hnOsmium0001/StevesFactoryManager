package vswe.stevesfactory.logic.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import vswe.stevesfactory.logic.FilterType;

import java.util.List;

public class SingleItemFilter {

    private ItemStack item;

    private boolean matchDamage;
    private boolean matchTag;

    private FilterType type = FilterType.WHITELIST;

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public boolean isMatchingDamage() {
        return matchDamage;
    }

    public void setMatchingDamage(boolean matchDamage) {
        this.matchDamage = matchDamage;
    }

    public boolean isMatchingTag() {
        return matchTag;
    }

    public void setMatchingTag(boolean matchTag) {
        this.matchTag = matchTag;
    }

    public boolean isMatchingAmount() {
        return item.getCount() != 0;
    }

    public void setMatchingAmount(boolean matchingAmount) {
        item.setCount(matchingAmount ? Integer.MAX_VALUE : 0);
    }

    public void setAmount(int amount) {
        item.setCount(amount);
    }

    public void read(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("Item"));
        item = new ItemStack(ForgeRegistries.ITEMS.getValue(id), 1);
        item.setTag(tag.getCompound("Tag"));
        item.setCount(tag.getInt("Count"));
    }

    public void write(CompoundNBT tag) {
        tag.putString("Item", item.getItem().getRegistryName().toString());
        tag.putInt("Count", item.getCount());
        if (item.getTag() != null) {
            tag.put("Tag", item.getTag());
        }
    }

    public boolean isContentEqual(SingleItemFilter other) {
        return item.isItemEqual(other.item) && item.areShareTagsEqual(other.item);
    }

    public boolean test(ItemStack stack) {
        if (!item.isItemEqual(stack)) {
            return false;
        }
        if (isMatchingAmount() && item.getCount() != stack.getCount()) {
            return false;
        }
        if (matchDamage && item.getDamage() != stack.getDamage()) {
            return false;
        }
        return !matchTag || (item.areShareTagsEqual(stack));
    }

    @SuppressWarnings("UnusedReturnValue")
    public List<ItemStack> extractFromInventory(List<ItemStack> target, IItemHandler handler, boolean simulate) {
        int desired = isMatchingAmount() ? item.getCount() : Integer.MAX_VALUE;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.extractItem(i, desired, simulate);
            if (stack.isEmpty() || doesItemsMatch(stack)) {
                continue;
            }
            desired -= stack.getCount();
            target.add(stack);
        }
        return target;
    }

    public ItemStack extractFromInventoryMerge(IItemHandler handler, boolean simulate) {
        int desired = isMatchingAmount() ? item.getCount() : Integer.MAX_VALUE;
        int extracted = 0;
        ItemStack result = ItemStack.EMPTY;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.extractItem(i, desired, simulate);
            if (stack.isEmpty() || doesItemsMatch(stack)) {
                continue;
            }
            extracted += stack.getCount();
            desired -= stack.getCount();

            // Optimization to reuse ItemStack object
            if (result.isEmpty()) {
                result = stack;
            }
        }

        // If extracted is non-zero, it means we have extracted at least one non-empty item stack, which means result won't be ItemStack.EMPTY
        // If we haven't extracted any non-empty item stack, extracted will be 0 anyways
        // Therefore this operation will not pollute ItemStack.EMPTY as long as the inventory works as documented
        result.setCount(extracted);
        return result;
    }

    private boolean doesItemsMatch(ItemStack second) {
        boolean equal = !item.isItemEqual(second)
                || (matchDamage && item.getDamage() != second.getDamage())
                || (matchTag && !item.areShareTagsEqual(second));
        return equal ^ getTypeFlag();
    }

    private boolean getTypeFlag() {
        switch (getType()) {
            case WHITELIST: return false;
            case BLACKLIST: return true;
        }
        throw new IllegalStateException();
    }
}
