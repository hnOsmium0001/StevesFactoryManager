package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.layout.properties.Side;
import vswe.stevesfactory.logic.procedure.IRecipeTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.List;

public class RecipeConfigurationMenu<P extends IProcedure & IProcedureClientData & IRecipeTarget> extends Menu<P> {

    private IngredientSlot[] ingredientSlots = new IngredientSlot[9];
    private ProductSlot productSlot;

    public RecipeConfigurationMenu() {
        for (int i = 0; i < ingredientSlots.length; i++) {
            ingredientSlots[i] = new IngredientSlot(ItemStack.EMPTY, i, () -> productSlot.evalCraftResult());
            addChildren(ingredientSlots[i]);
        }
        productSlot = new ProductSlot(ItemStack.EMPTY);
        addChildren(productSlot);
        reflow();
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        P procedure = getLinkedProcedure();
        for (IngredientSlot slot : ingredientSlots) {
            slot.setRecipeHandler(procedure);
            slot.setStack(procedure.getIngredient(slot.slot));
        }
        productSlot.setRecipeHandler(procedure);
        productSlot.setStack(productSlot.evalCraftResult());
    }

    @Override
    public void reflow() {
        int x = 4;
        int y = HEADING_BOX.getPortionHeight() + 4;
        int i = 1;
        for (IngredientSlot slot : ingredientSlots) {
            slot.setLocation(x, y);
            if (i % 3 == 0) {
                x = 4;
                y += slot.getWidth() + 4;
            } else {
                x += slot.getHeight() + 4;
            }
            i++;
        }

        // + + +
        // + + + <<< this one is the 5th
        // + + +
        productSlot.alignTo(ingredientSlots[5], Side.RIGHT, HorizontalAlignment.CENTER);
        productSlot.moveX(4);
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.RecipeConfiguration");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        if (productSlot.getCraftResult().isEmpty()) {
            errors.add(I18n.format("error.sfm.CraftingProcedure.NoRecipe"));
        }
        return errors;
    }
}
