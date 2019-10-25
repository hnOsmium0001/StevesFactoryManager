package vswe.stevesfactory.logic.procedure;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;

public interface IRecipeTarget {

    /**
     * Get the internal inventory handler for recipes uses. The return value of this method should not be mutated, if the provider detects
     * such mutation it should throw an exception.
     */
    CraftingInventory getInventory();

    ItemStack getIngredient(int slot);

    void setIngredient(int slot, ItemStack ingredient);
}
