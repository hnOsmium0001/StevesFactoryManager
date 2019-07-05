package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.IWidget;

public interface LeafWidgetMixin extends IWidget {

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY) {
        return false;
    }

    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled) {
        return false;
    }

    @Override
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    default boolean charTyped(char charTyped, int keyCode) {
        return false;
    }

}
