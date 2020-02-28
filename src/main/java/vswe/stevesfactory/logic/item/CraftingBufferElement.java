package vswe.stevesfactory.logic.item;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.item.IItemBuffer;

import java.util.Map;

public class CraftingBufferElement implements IItemBuffer {

    private final IExecutionContext context;

    private RecipeInfo recipe;
    private int outputBase = -1;
    private ItemStack result = ItemStack.EMPTY;

    public CraftingBufferElement(IExecutionContext context) {
        this.context = context;
    }

    public ICraftingRecipe getRecipe() {
        return recipe.getRecipe();
    }

    public void setRecipe(RecipeInfo recipe) {
        this.recipe = recipe;
        // TODO crafting inventory
        result = recipe.getRecipe().getCraftingResult(null);
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
        for (Object2IntMap.Entry<Ingredient> ingredient : recipe.getIngredients()) {
            // Consumption of items per craft
            int consumption = ingredient.getIntValue();
            // Number of all available items, considering all alternatives
            int available = 0;
            for (ItemStack matchable : ingredient.getKey().getMatchingStacks()) {
                DirectBufferElement buffer = buffers.get(matchable.getItem());
                if (buffer == null) {
                    continue;
                }

                available += buffer.getStack().getCount();
            }

            // Number of crafting set performable, just looking at this ingredient
            int totalAvailableSets = available / consumption;
            if (totalAvailableSets == 0) {
                // Fast path for resulting nothing: cannot find any stacks for this ingredient, therefore it will be the limiting reagent
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
        // Number of crafts needed to produced the parameter number of products (`amount`)
        int actualSize = amount / outputBase;
        Map<Item, DirectBufferElement> buffers = context.getItemBuffers(DirectBufferElement.class);
        for (Object2IntMap.Entry<Ingredient> ingredient : recipe.getIngredients()) {
            // Total item consumption for this ingredient for the given `actualSize` number of crafts
            int desire = actualSize * ingredient.getIntValue();
            for (ItemStack matchable : ingredient.getKey().getMatchingStacks()) {
                DirectBufferElement buffer = buffers.get(matchable.getItem());
                if (buffer == null) {
                    continue;
                }

                int consumption = Math.min(buffer.stack.getCount(), desire); // Prevent over using an ingredient
                buffer.use(consumption);
                buffer.stack.shrink(consumption);
                desire -= consumption;
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
