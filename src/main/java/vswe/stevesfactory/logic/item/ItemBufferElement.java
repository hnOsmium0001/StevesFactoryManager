package vswe.stevesfactory.logic.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.api.item.IItemBufferElement;

import java.util.HashSet;
import java.util.Set;

public class ItemBufferElement implements IItemBufferElement {

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

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getUsed() {
        return used;
    }

    @Override
    public void setUsed(int used) {
        this.used = used;
    }

    @Override
    public void use(int amount) {
        used += amount;
    }

    @Override
    public void put(int amount) {
        used -= amount;
    }

    @Override
    public int getEvaluationPriority() {
        return 0;
    }
}
