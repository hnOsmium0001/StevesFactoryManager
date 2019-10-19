package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.logic.procedure.IRecipeTarget;

public class IngredientSlot extends ConfigurationSlot<IWidget> {

    public final int slot;
    public final Runnable onIngredientChanged;
    private IRecipeTarget recipeHandler;

    public IngredientSlot(ItemStack stack, int slot, Runnable onIngredientChanged) {
        super(stack);
        this.slot = slot;
        this.onIngredientChanged = onIngredientChanged;
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
    protected void onRightClick() {
        stack = ItemStack.EMPTY;
        onSetStack();
    }

    @Override
    protected void onSetStack() {
        recipeHandler.setIngredient(slot, stack);
        onIngredientChanged.run();
    }

    public IRecipeTarget getRecipeHandler() {
        return recipeHandler;
    }

    public void setRecipeHandler(IRecipeTarget recipeHandler) {
        this.recipeHandler = recipeHandler;
    }
}
