package vswe.stevesfactory.logic.procedure;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.item.ItemBuffers;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.item.CraftingBufferElement;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.RecipeConfigurationMenu;
import vswe.stevesfactory.utils.MyCraftingInventory;
import vswe.stevesfactory.utils.NetworkHelper;

import java.util.Optional;

public class CraftingProcedure extends AbstractProcedure implements IRecipeTarget {

    private ICraftingRecipe recipe;
    private CraftingInventory inventory = new MyCraftingInventory();

    public CraftingProcedure() {
        super(Procedures.CRAFTING.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
        updateRecipe(context);
        if (hasError()) {
            return;
        }

        CraftingBufferElement buffer = new CraftingBufferElement(context);
        buffer.setRecipe(recipe);
        ItemBuffers container = NetworkHelper.getOrCreateBufferContainer(context.getItemBuffers(), buffer.getStack().getItem());
        container.putBuffer(CraftingBufferElement.class, buffer);
    }

    private void updateRecipe(IExecutionContext context) {
        if (recipe == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            // In all cases we will not get null from the above invocation if we are on a server thread
            Preconditions.checkState(server != null, "Illegal to execute procedure on client side");
            Optional<ICraftingRecipe> recipe = server.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, context.getControllerWorld());
            this.recipe = recipe.orElse(null);
        }
    }

    public boolean hasError() {
        return recipe == null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public FlowComponent<CraftingProcedure> createFlowComponent() {
        FlowComponent<CraftingProcedure> f = FlowComponent.of(this);
        f.addMenu(new RecipeConfigurationMenu<>());
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
