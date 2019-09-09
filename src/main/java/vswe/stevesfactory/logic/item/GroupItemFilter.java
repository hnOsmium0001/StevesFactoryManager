package vswe.stevesfactory.logic.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.logic.FilterType;

import java.util.ArrayList;
import java.util.List;

public class GroupItemFilter {

    private FilterType type = FilterType.WHITELIST;

    private List<ItemStack> items = new ArrayList<>();

    private boolean matchDamage;
    private boolean matchTag;
    private boolean matchingAmount;

    public FilterType getType() {
        return type;
    }

    public boolean isMatchingDamage() {
        return matchDamage;
    }

    public void setMatchingDamage(boolean matchingDamage) {
        this.matchDamage = matchingDamage;
    }

    public boolean isMatchingTag() {
        return matchTag;
    }

    public void setMatchingTag(boolean matchingTag) {
        this.matchTag = matchingTag;
    }

    public boolean isMatchingAmount() {
        return matchingAmount;
    }

    public void setMatchingAmount(boolean matchingAmount) {
        this.matchingAmount = matchingAmount;
    }

    public boolean test(ItemStack stack) {
        for (int i = 0; i < items.size(); i++) {
            boolean b = isEqual(i, stack);
            if (b) {
                switch (type) {
                    case WHITELIST: return true;
                    case BLACKLIST: return false;
                }
            }
        }

        switch (type) {
            case WHITELIST: return false;
            case BLACKLIST: return true;
        }
        throw new IllegalStateException();
    }

    public void extractFromInventory(List<ItemStack> target, IItemHandler handler) {
        Object2IntMap<Item> counts = new Object2IntOpenHashMap<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.extractItem(i, Integer.MAX_VALUE, true);
            if (stack.isEmpty()) {
                continue;
            }

            int count = stack.getCount();
            Item item = stack.getItem();
            int collectedCount = counts.getInt(item);

            boolean b = test(stack);
            // TODO
        }
    }

    private boolean isEqual(int filterIndex, ItemStack stack) {
        ItemStack item = items.get(filterIndex);
        return !item.isItemEqual(stack)
                || (matchDamage && item.getDamage() != stack.getDamage())
                || (matchTag && !item.areShareTagsEqual(stack));
    }

    private boolean doesItemMatch(int filterIndex, ItemStack stack) {
        return isEqual(filterIndex, stack) ^ getTypeFlag();
    }

    private boolean getTypeFlag() {
        switch (getType()) {
            case WHITELIST: return false;
            case BLACKLIST: return true;
        }
        throw new IllegalStateException();
    }

    public void read(CompoundNBT tag) {

    }

    public void write(CompoundNBT tag) {

    }
}
