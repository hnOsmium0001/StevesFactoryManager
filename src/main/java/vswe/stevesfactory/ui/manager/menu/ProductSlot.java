package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.logic.procedure.IRecipeTarget;

import java.util.Optional;

public class ProductSlot extends ConfigurationSlot<IWidget> {

    private IRecipeTarget recipeHandler;

    public ProductSlot(ItemStack stack) {
        super(stack);
    }

    @Override
    protected boolean hasEditor() {
        return false;
    }

    @Override
    protected IWidget createEditor() {
        return null;
    }

    @Override
    protected void onLeftClick() {
        // No inventory selection dialog for the product slot
    }

    public ItemStack evalCraftResult() {
        ClientWorld world = Minecraft.getInstance().world;
        CraftingInventory inventory = getRecipeHandler().getInventory();
        return stack = world.getRecipeManager()
                .getRecipe(IRecipeType.CRAFTING, inventory, world)
                .map(r -> r.getCraftingResult(inventory))
                .orElse(ItemStack.EMPTY);
    }

    public ItemStack getCraftResult() {
        return stack;
    }

    public IRecipeTarget getRecipeHandler() {
        return recipeHandler;
    }

    public void setRecipeHandler(IRecipeTarget recipeHandler) {
        this.recipeHandler = recipeHandler;
    }
}
