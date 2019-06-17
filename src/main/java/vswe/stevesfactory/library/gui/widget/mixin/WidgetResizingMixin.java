package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.core.IResizableWidget;

public interface WidgetResizingMixin extends IResizableWidget {

    @Override
    default void setWidth(int width) {
        getDimensions().width = width;
    }

    @Override
    default void setHeight(int height) {
        getDimensions().height = height;
    }

}
