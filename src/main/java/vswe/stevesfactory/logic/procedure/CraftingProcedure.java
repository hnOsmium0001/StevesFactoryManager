package vswe.stevesfactory.logic.procedure;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.item.CraftingBufferElement;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.RecipeConfigurationMenu;
import vswe.stevesfactory.utils.IOHelper;
import vswe.stevesfactory.utils.MyCraftingInventory;

import java.util.Optional;

public class CraftingProcedure extends AbstractProcedure implements IRecipeTarget {

    private transient ICraftingRecipe recipe;
    private MyCraftingInventory inventory = new MyCraftingInventory();

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
        context.getItemBuffers(CraftingBufferElement.class)
                .put(buffer.getStack().getItem(), buffer);
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
        // Error for execution (server side)
        return recipe == null;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<CraftingProcedure> createFlowComponent() {
        FlowComponent<CraftingProcedure> f = FlowComponent.of(this);
        f.addMenu(new RecipeConfigurationMenu<>());
        return f;
    }

    @Override
    public CraftingInventory getInventory() {
        return inventory;
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

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("RecipeInv", IOHelper.writeItemStacks(inventory.handle));
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        inventory = IOHelper.readInventory(tag.getList("RecipeInv", Constants.NBT.TAG_COMPOUND), new MyCraftingInventory());
    }
}
