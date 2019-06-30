package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.core.IContainer;
import vswe.stevesfactory.library.gui.core.IWidget;

public interface ContainerWidgetMixin<T extends IWidget> extends IContainer<T> {

    @Override
    default void render(int mouseX, int mouseY, float particleTicks) {
        for (T child : getChildren()) {
            child.render(mouseX, mouseY, particleTicks);
        }
    }

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (T child : getChildren()) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (T child : getChildren()) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (T child : getChildren()) {
            if (child.mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (T child : getChildren()) {
            if (child.mouseScrolled(mouseX, mouseY, amountScrolled)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (T child : getChildren()) {
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (T child : getChildren()) {
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean charTyped(char charTyped, int keyCode) {
        for (T child : getChildren()) {
            if (child.charTyped(charTyped, keyCode)) {
                return true;
            }
        }
        return false;
    }

}
