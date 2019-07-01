package vswe.stevesfactory.library.gui.layout.flow;

import vswe.stevesfactory.library.gui.core.ILayout;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.layout.BoxSizing;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import java.awt.*;
import java.util.List;

public class BasicFlowLayout<T extends IWidget & RelocatableWidgetMixin> implements ILayout<T> {

    public static final BasicFlowLayout<RelocatableWidgetMixin> INSTANCE = new BasicFlowLayout<>();

    @Override
    public List<T> reflow(Dimension bounds, List<T> widgets) {
        int y = 0;
        for (T widget : widgets) {
            if (BoxSizing.shouldIncludeWidget(widget)) {
                adjustPosition(widget, y);
                y += widget.getHeight();
            }
        }
        return widgets;
    }

    @Override
    public LayoutType getType() {
        return LayoutType.StatelessLayout;
    }

    /**
     * This method is to be overridden to provide more functionality when reflow.
     */
    public void adjustPosition(T widget, int y) {
        widget.setY(y);
    }

}
