package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.*;

public interface NestedEventHandlerMixin extends IWindow {

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isInside(mouseX, mouseY)) {
                continue;
            }
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
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isInside(mouseX, mouseY)) {
                continue;
            }
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isFocused()) {
                continue;
            }
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isInside(mouseX, mouseY)) {
                continue;
            }
            if (child.mouseScrolled(mouseX, mouseY, scroll)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isFocused()) {
                continue;
            }
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isFocused()) {
                continue;
            }
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean charTyped(char charTyped, int keyCode) {
        for (IWidget child : getChildren()) {
            if (!(child instanceof IContainer<?>) && !child.isFocused()) {
                continue;
            }
            if (child.charTyped(charTyped, keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default void mouseMoved(double mouseX, double mouseY) {
        for (IWidget child : getChildren()) {
            child.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    default void update(float particleTicks) {
        for (IWidget child : getChildren()) {
            child.update(particleTicks);
        }
    }

    @Override
    default void onRemoved() {
        for (IWidget child : getChildren()) {
            child.onRemoved();
        }
    }
}
