package vswe.stevesfactory.logic.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import vswe.stevesfactory.api.item.IItemBufferElement;

import java.util.*;

// TODO
public class CraftingBufferElement implements IItemBufferElement {

    private final Map<Item, IItemBufferElement> buffers;
    private IRecipe<?> recipe;

    public CraftingBufferElement(Map<Item, IItemBufferElement> buffers) {
        this.buffers = buffers;
    }

    @Override
    public int getEvaluationPriority() {
        // After direct-sourced buffers
        return 1;
    }

    @Override
    public ItemStack getStack() {
        return null;
    }

    @Override
    public void setStack(ItemStack stack) {

    }

    @Override
    public int getUsed() {
        return 0;
    }

    @Override
    public void setUsed(int used) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void use(int amount) {
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack stack : ingredient.getMatchingStacks()) {
                IItemBufferElement bufferElement = buffers.get(stack.getItem());
                if (bufferElement != null) {
                    // TODO partial consumption
                    int available = bufferElement.getStack().getCount();
                    bufferElement.use(Math.min(available, amount));
                    break;
                }
            }
        }
    }

    @Override
    public void put(int amount) {
        throw new UnsupportedOperationException();
    }
}
