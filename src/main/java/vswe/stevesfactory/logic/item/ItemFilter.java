package vswe.stevesfactory.logic.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemFilter {

    private ItemStack item;
    private int amount;

//    public List<String> getMouseOver() {
//        if (!item.isEmpty() && GuiScreen.isShiftKeyDown()) {
//            return ComponentMenuItem.getToolTip(item);
//        }
//
//        List<String> ret = new ArrayList<String>();
//
//        if (item.isEmpty()) {
//            ret.add(Localization.NO_ITEM_SELECTED.toString());
//        } else {
//            ret.add(ComponentMenuItem.getDisplayName(item));
//        }
//
//        ret.add("");
//        ret.add(Localization.CHANGE_ITEM.toString());
//        if (!item.isEmpty()) {
//            ret.add(Localization.EDIT_SETTING.toString());
//            ret.add(Localization.FULL_DESCRIPTION.toString());
//        }
//
//        return ret;
//    }

    public void clear() {
        item = ItemStack.EMPTY;
        amount = 1;
    }

    public int getAmount() {
        return item.isEmpty() ? 0 : amount;
    }

    public void setAmount(int val) {
        if (!item.isEmpty()) {
            amount = val;
        }
    }

    public boolean isValid() {
        return !item.isEmpty();
    }

    public ItemStack getItem() {
        return item.copy();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getActualItem() {
        ItemStack copy = item.copy();
        copy.setCount(amount);
        return copy;
    }

    public int getDefaultAmount() {
        return 1;
    }

    public void read(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("Item"));
        item = new ItemStack(ForgeRegistries.ITEMS.getValue(id), 1);
        amount = tag.getShort("Count");

        item.setTag(tag.getCompound("Tag"));
    }

    public void write(CompoundNBT tag) {
        tag.putString("Item", item.getItem().getRegistryName().toString());
        tag.putShort("Count", (short) amount);
        if (item.getTag() != null) {
            tag.put("Tag", item.getTag());
        }
    }

    public boolean isContentEqual(ItemFilter otherSetting) {
        return Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(otherSetting.item.getItem()) && ItemStack.areItemStackTagsEqual(item, ((ItemFilter) otherSetting).item);
    }

    public boolean test(ItemStack stack) {
        // TODO
        return false;
    }
}
