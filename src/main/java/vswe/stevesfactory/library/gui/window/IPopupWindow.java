package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.IWindow;

public interface IPopupWindow extends IWindow {

    void initialize(int id);

    /**
     * Number of ticks this popup should live. Return {@code -1} to remove the lifespan limit.
     */
    int getLifespan();

    boolean isDraggable();

    boolean shouldDrag(double mouseX, double mouseY);

    default void move(int xOffset, int yOffset) {
        setPosition(getX() + xOffset, getY() + yOffset);
    }
}
