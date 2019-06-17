package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.core.IRelocatableWidget;

public interface WidgetRelocationMixin extends IRelocatableWidget {

    @Override
    default void setX(int x) {
        getLocation().x = x;
    }

    @Override
    default void setY(int y) {
        getLocation().y = y;
    }

}
