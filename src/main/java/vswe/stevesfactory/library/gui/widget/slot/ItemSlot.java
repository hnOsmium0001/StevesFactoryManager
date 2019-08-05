package vswe.stevesfactory.library.gui.widget.slot;

import net.minecraft.item.ItemStack;

import java.util.function.IntConsumer;

public class ItemSlot extends AbstractSlot {

    private ItemStack renderedStack;
    private IntConsumer action;

    public ItemSlot(ItemStack renderedStack, IntConsumer action) {
        this.renderedStack = renderedStack;
        this.action = action;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        action.accept(button);
        return true;
    }

    @Override
    public ItemStack getRenderedStack() {
        return renderedStack;
    }

    public void setRenderedStack(ItemStack renderedStack) {
        this.renderedStack = renderedStack;
    }
}
