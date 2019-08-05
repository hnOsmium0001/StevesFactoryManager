package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.IWindow;

public interface IPopupWindow extends IWindow {

    /**
     * Number of ticks this popup should live. Return {@code -1} to remove the lifespan limit.
     */
    int getLifespan();

    boolean shouldDrag(double mouseX, double mouseY);

    DiscardCondition getDiscardCondition();

    default void move(int xOffset, int yOffset) {
        setPosition(getX() + xOffset, getY() + yOffset);
    }
}
