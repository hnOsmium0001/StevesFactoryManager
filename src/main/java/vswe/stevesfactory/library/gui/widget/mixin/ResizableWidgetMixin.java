package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.widget.IWidget;

import java.awt.*;

public interface ResizableWidgetMixin extends IWidget {

    default void setDimensions(Dimension dimensions) {
        setDimensions(dimensions.width, dimensions.height);
    }

    default void setDimensions(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    default void setWidth(int width) {
        getDimensions().width = width;
    }

    default void setHeight(int height) {
        getDimensions().height = height;
    }
}
