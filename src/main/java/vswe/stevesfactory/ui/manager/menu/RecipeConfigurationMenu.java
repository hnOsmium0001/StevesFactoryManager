package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.layout.properties.Side;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.logic.procedure.IRecipeTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;
import vswe.stevesfactory.utils.MyCraftingInventory;

import java.util.Optional;

public class RecipeConfigurationMenu<P extends IProcedure & IProcedureClientData & IRecipeTarget> extends Menu<P> {

    private IngredientSlot[] ingredientSlots = new IngredientSlot[9];
    private ProductSlot productSlot;

    public RecipeConfigurationMenu() {
        for (int i = 0; i < ingredientSlots.length; i++) {
            ingredientSlots[i] = new IngredientSlot(ItemStack.EMPTY, i, this::evalCraftingProduct);
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
            slot.stack = procedure.getIngredient(slot.slot);
            slot.recipeHandler = procedure;
        }
        productSlot.stack = evalCraftingProduct();
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
    protected void updateData() {
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menus.RecipeConfiguration");
    }

    private CraftingInventory asCraftingInventory() {
        CraftingInventory inventory = new MyCraftingInventory();
        for (IngredientSlot slot : ingredientSlots) {
            inventory.setInventorySlotContents(slot.slot, slot.stack);
        }
        return inventory;
    }

    private ItemStack evalCraftingProduct() {
        CraftingInventory inv = asCraftingInventory();
        ClientWorld world = Minecraft.getInstance().world;
        Optional<ICraftingRecipe> recipe = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, world);
        productSlot.stack = recipe.map(r -> r.getCraftingResult(inv)).orElse(ItemStack.EMPTY);
        return productSlot.stack;
    }

    public static class IngredientSlot extends ConfigurationSlot<IWidget> {

        private final int slot;
        private final Runnable evalProduct;
        private IRecipeTarget recipeHandler;

        public IngredientSlot(ItemStack stack, int slot, Runnable evalProduct) {
            super(stack);
            this.slot = slot;
            this.evalProduct = evalProduct;
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
        protected void onSetStack() {
            recipeHandler.setIngredient(slot, stack);
            evalProduct.run();
        }
    }

    public static class ProductSlot extends ConfigurationSlot<IWidget> {

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
        protected void onRightClick() {
        }
    }
}
