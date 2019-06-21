package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.core.ILayout;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import java.awt.*;
import java.util.List;

public class UnruledFlowLayout<T extends IWidget & RelocatableWidgetMixin> implements ILayout<T> {

    @Override
    public List<T> reflow(Dimension bounds, List<T> widgets) {
        int y = 0;
        for (T widget : widgets) {
            adjustPosition(widget, y);
            y += widget.getHeight();
        }
        return widgets;
    }

    @Override
    public LayoutType getType() {
        return LayoutType.StatelessLayout;
    }

    public void adjustPosition(T widget, int y) {
        widget.setY(y);
    }

}
