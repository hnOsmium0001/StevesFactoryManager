package vswe.stevesfactory.logic.item;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import vswe.stevesfactory.utils.Utils;

import java.util.function.Function;
import java.util.stream.Collectors;

public class RecipeInfo {

    private final ICraftingRecipe recipe;
    private final ImmutableList<Object2IntMap.Entry<Ingredient>> ingredients;

    public RecipeInfo(ICraftingRecipe recipe) {
        this.recipe = recipe;
        //noinspection UnstableApiUsage
        this.ingredients = recipe.getIngredients().stream()
                // Filter so that weird empty ingredients doesn't break collecting ingredient buffers
                .filter(Utils.not(Ingredient::hasNoMatchingItems))
                // Turn List<Ingredient> with repeats to Map<Ingredient, Long> with value is counting of occurrences
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                // Turn Map.Entry<Ingredient, Long> to Object2IntMap.Entry<Ingredient>
                // one for convenience of using int instead of long
                // two for avoiding wrapper class
                .entrySet().stream()
                .map(entry -> new AbstractObject2IntMap.BasicEntry<>(entry.getKey(), entry.getValue().intValue()))
                // Collect to an ImmutableList because we don't need a set
                .collect(ImmutableList.toImmutableList());
    }

    public ICraftingRecipe getRecipe() {
        return recipe;
    }

    public ImmutableList<Object2IntMap.Entry<Ingredient>> getIngredients() {
        return ingredients;
    }
}
