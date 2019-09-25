package vswe.stevesfactory.logic.item;

import net.minecraft.item.ItemStack;

public interface IItemBufferElement extends Comparable<IItemBufferElement> {

    ItemStack getStack();

    void setStack(ItemStack stack);

    int getUsed();

    void setUsed(int used);

    void use(int amount);

    void put(int amount);

    int getEvaluationPriority();

    @Override
    default int compareTo(IItemBufferElement other) {
        return this.getEvaluationPriority() - other.getEvaluationPriority();
    }
}
