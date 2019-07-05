package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import java.awt.*;
import java.util.List;

public class FlowLayout {

    public static final FlowLayout INSTANCE = new FlowLayout();

    protected FlowLayout() {
    }

    public <T extends IWidget & RelocatableWidgetMixin> List<T> reflow(Dimension bounds, List<T> widgets) {
        int y = 0;
        for (T widget : widgets) {
            if (BoxSizing.shouldIncludeWidget(widget)) {
                adjustPosition(widget, y);
                y += widget.getHeight();
            }
        }
        return widgets;
    }

    /**
     * This method is to be overridden to provide more functionality when reflow.
     */
    public <T extends IWidget & RelocatableWidgetMixin> void adjustPosition(T widget, int y) {
        widget.setY(y);
    }
}
