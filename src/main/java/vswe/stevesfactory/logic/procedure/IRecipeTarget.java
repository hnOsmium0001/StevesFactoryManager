package vswe.stevesfactory.logic.procedure;

import net.minecraft.item.ItemStack;

public interface IRecipeTarget {

    ItemStack getIngredient(int slot);

    void setIngredient(int slot, ItemStack ingredient);
}
