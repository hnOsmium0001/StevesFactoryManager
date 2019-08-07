/* Class is adatped from org.cyclops.commoncapabilities.api.capability.itemhandler.DefaultSlotlessItemHandlerWrapper
 * https://github.com/CyclopsMC/CommonCapabilitiesAPI/blob/master-1.12/capability/itemhandler/DefaultSlotlessItemHandlerWrapper.java
 */
package vswe.stevesfactory.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

// This should be eventually replaced with the SlotlessItemHandler from CommonCapabilities once it's ported to 1.14
public class SlotlessItemHandlerWrapper implements IItemHandler {

    /**
     * Convenience value matching any ItemStack.
     */
    public static final int ANY = 0;
    /**
     * Match ItemStack items.
     */
    public static final int ITEM = 1;
    /**
     * Match ItemStack damage values.
     */
    public static final int DAMAGE = 2;
    /**
     * Match ItemStack NBT tags.
     */
    public static final int NBT = 4;
    /**
     * Match ItemStack stacksizes.
     */
    public static final int STACKSIZE = 8;
    /**
     * Convenience value matching ItemStacks exactly by item, damage value, NBT tag and stacksize.
     */
    public static final int EXACT = ITEM | DAMAGE | NBT | STACKSIZE;

    public static boolean areItemStacksEqual(ItemStack a, ItemStack b, int matchFlags) {
        if (matchFlags == ANY) {
            return true;
        }
        boolean item = (matchFlags & ITEM) > 0;
        boolean damage = (matchFlags & DAMAGE) > 0
                && !(a.getDamage() == 0
                || b.getDamage() == 0);
        boolean nbt = (matchFlags & NBT) > 0;
        boolean stackSize = (matchFlags & STACKSIZE) > 0;
        return a == b || a.isEmpty() && b.isEmpty() ||
                (!a.isEmpty() && !b.isEmpty()
                        && (!item || a.getItem() == b.getItem())
                        && (!damage || a.getDamage() == b.getDamage())
                        && (!stackSize || a.getCount() == b.getCount())
                        && (!nbt || areItemStackTagsEqual(a, b)));
    }

    public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b) {
        if ((a.getTag() == null && b.getTag() != null)
                || a.getTag() != null && b.getTag() == null) {
            return false;
        } else {
//            return (a.getTag() == null || NBT_COMPARATOR.compare(a.getTag(), b.getTag()) == 0);
            // We don't include a.areCapsCompatible(b), because we expect that differing caps have different NBT tags.
            // TODO port NBTBaseComparator
            return a.getTag() == null || a.getTag().equals(b.getTag());
        }
    }

    private IItemHandler wrapped;

    public SlotlessItemHandlerWrapper(IItemHandler wrapped) {
        this.wrapped = wrapped;
    }

    // Slotless item handler methods

    public IItemHandler getWrappedItemHandler() {
        return wrapped;
    }

    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        for (int i = 0; i < getWrappedItemHandler().getSlots(); i++) {
            stack = getWrappedItemHandler().insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Nonnull
    public ItemStack extractItem(int amount, boolean simulate) {
        IItemHandler itemHandler = getWrappedItemHandler();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.extractItem(i, amount, simulate);
            if (!itemStack.isEmpty()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, boolean simulate) {
        IItemHandler itemHandler = getWrappedItemHandler();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            int amount = matchStack.getCount();
            ItemStack tempItemStack;
            if (simulate || (!(tempItemStack = itemHandler.extractItem(i, amount, true)).isEmpty()
                    && areItemStacksEqual(matchStack, tempItemStack, matchFlags))) {
                ItemStack itemStack = itemHandler.extractItem(i, amount, simulate);
                if (!itemStack.isEmpty() && areItemStacksEqual(matchStack, itemStack, matchFlags)) {
                    return itemStack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public int getLimit() {
        IItemHandler itemHandler = getWrappedItemHandler();
        int total = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            total += itemHandler.getSlotLimit(i);
        }
        return total;
    }

    // Regular item handler methods

    @Override
    public int getSlots() {
        return wrapped.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return wrapped.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return wrapped.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return wrapped.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return wrapped.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return wrapped.isItemValid(slot, stack);
    }
}
