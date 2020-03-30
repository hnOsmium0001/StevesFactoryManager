package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.screen.WidgetScreen;

import javax.annotation.Nonnull;

public interface IPopupWindow extends IWindow, Comparable<IPopupWindow> {

    boolean shouldDiscard();

    default void move(int xOffset, int yOffset) {
        setPosition(getX() + xOffset, getY() + yOffset);
    }

    int getOrder();

    void setOrder(int order);

    @Override
    default int compareTo(@Nonnull IPopupWindow that) {
        return this.getOrder() - that.getOrder();
    }

    default void onAdded(WidgetScreen<?> cWidgetScreen) {
    }
}
