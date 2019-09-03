package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.IWindow;

public interface IPopupWindow extends IWindow {

    boolean shouldDiscard();

    default void move(int xOffset, int yOffset) {
        setPosition(getX() + xOffset, getY() + yOffset);
    }
}
