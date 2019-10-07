package vswe.stevesfactory.logic.procedure;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

public class CraftingProcedure extends AbstractProcedure implements IRecipeTarget {

    private ICraftingRecipe recipe;
    private CraftingInventory inventory = new CraftingInventory(null, 0, 0);

    public CraftingProcedure() {
        super(Procedures.CRAFTING.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
        updateRecipe();
        if (hasError()) {
            return;
        }
    }

    private void updateRecipe() {
        if (recipe == null) {
            //  TODO
        }
    }

    public boolean hasError() {
        return recipe != null;
    }

    @Override
    public FlowComponent<CraftingProcedure> createFlowComponent() {
        FlowComponent<CraftingProcedure> f = FlowComponent.of(this);
        return f;
    }

    @Override
    public ItemStack getIngredient(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public void setIngredient(int slot, ItemStack ingredient) {
        inventory.setInventorySlotContents(slot, ingredient);
        recipe = null;
    }
}
