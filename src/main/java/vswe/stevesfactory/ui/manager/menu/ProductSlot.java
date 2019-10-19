package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.widget.IWidget;

public class ProductSlot extends ConfigurationSlot<IWidget> {

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
    protected void onLeftClick() {
        // No inventory selection dialog for the product slot
    }
}
