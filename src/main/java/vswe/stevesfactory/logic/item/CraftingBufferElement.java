package vswe.stevesfactory.logic.item;

import com.google.common.base.Preconditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import vswe.stevesfactory.api.item.IItemBufferElement;
import vswe.stevesfactory.api.logic.IExecutionContext;

import java.util.Map;

// TODO fix dupes and crashes
public class CraftingBufferElement implements IItemBufferElement {

    private final IExecutionContext context;

    private ICraftingRecipe recipe;
    private int outputBase = -1;
    private ItemStack result = ItemStack.EMPTY;

    public CraftingBufferElement(IExecutionContext context) {
        this.context = context;
    }

    public ICraftingRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(ICraftingRecipe recipe) {
        this.recipe = recipe;
        // TODO crafting inventory
        result = recipe.getCraftingResult(null);
        outputBase = result.getCount();
    }

    @Override
    public ItemStack getStack() {
        refresh();
        return result;
    }

    @Override
    public void setStack(ItemStack stack) {
        Preconditions.checkArgument(result.isItemEqual(stack) || stack.isEmpty());
        this.result = stack;
    }

    @Override
    public int getUsed() {
        return 0;
    }

    @Override
    public void setUsed(int used) {
        throw new UnsupportedOperationException();
    }

    public void refresh() {
        Map<Item, DirectBufferElement> buffers = context.getItemBuffers(DirectBufferElement.class);
        int batchesAvailable = Integer.MAX_VALUE;
        for (Ingredient ingredient : recipe.getIngredients()) {
            // Number of crafting set performable, just looking at this ingredient
            int totalAvailableSets = 0;
            for (ItemStack matchable : ingredient.getMatchingStacks()) {
                DirectBufferElement buffer = buffers.get(matchable.getItem());
                if (buffer == null) {
                    continue;
                }

                ItemStack source = buffer.getStack();
                // The total number of this ingredients needed in this recipe
                int needed = matchable.getCount();
                // Number of available resource
                int available = source.getCount();
                totalAvailableSets += available / needed;
            }

            if (totalAvailableSets == 0) {
                // Fast path for resulting nothing: cannot find any stacks for this ingredient, therefore this will be the limiting reagent
                batchesAvailable = 0;
                break;
            } else {
                batchesAvailable = Math.min(batchesAvailable, totalAvailableSets);
            }
        }
        result.setCount(outputBase * batchesAvailable);
    }

    @Override
    public void use(int amount) {
        int actualSize = amount / outputBase;
        Map<Item, DirectBufferElement> buffers = context.getItemBuffers(DirectBufferElement.class);
        for (Ingredient ingredient : recipe.getIngredients()) {
            int required = actualSize;
            for (ItemStack matchable : ingredient.getMatchingStacks()) {
                DirectBufferElement buffer = buffers.get(matchable.getItem());
                if (buffer == null) {
                    continue;
                }
                int consumption = Math.min(buffer.stack.getCount(), required);
                buffer.use(consumption);
                buffer.stack.shrink(consumption);
                required -= consumption;
            }
        }
    }

    @Override
    public void put(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanup() {
    }
}
