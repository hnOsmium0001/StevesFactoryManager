package vswe.stevesfactory.logic.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class SingleItemBufferElement {

    public final IItemHandler inventory;
    public final int slot;

    public ItemStack stack;
    public int used;

    public SingleItemBufferElement(ItemStack stack, IItemHandler inventory, int slot) {
        this.stack = stack;
        this.inventory = inventory;
        this.slot = slot;
    }
}
