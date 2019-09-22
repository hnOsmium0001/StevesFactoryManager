package vswe.stevesfactory.logic.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class ItemBufferElement {

    public final Set<Pair<IItemHandler, Integer>> inventories = new HashSet<>();

    public ItemStack stack;
    public int used = 0;

    public ItemBufferElement(ItemStack stack) {
        this.stack = stack;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ItemBufferElement addInventory(IItemHandler handler, int slot) {
        inventories.add(Pair.of(handler, slot));
        return this;
    }
}
