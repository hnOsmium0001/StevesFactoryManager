package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.utils.Utils;

import java.awt.*;
import java.util.List;

public class FlowLayout {

    private FlowLayout() {
    }

    public static <T extends IWidget> List<T> vertical(List<T> widgets, int x, int y, int gap) {
        for (T widget : widgets) {
            if (BoxSizing.shouldIncludeWidget(widget)) {
                widget.setLocation(x, y);
                y += widget.getHeight() + gap;
            }
        }
        return widgets;
    }

    public static <T extends IWidget> List<T> vertical(Dimension bounds, HorizontalAlignment alignment, List<T> widgets) {
        int y = 0;
        for (T widget : widgets) {
            if (BoxSizing.shouldIncludeWidget(widget)) {
                switch (alignment) {
                    case LEFT: {
                        widget.setLocation(0, y);
                        break;
                    }
                    case CENTER: {
                        int x = RenderingHelper.getXForAlignedCenter(0, bounds.width, widget.getWidth());
                        widget.setLocation(x, y);
                        break;
                    }
                    case RIGHT: {
                        int x = RenderingHelper.getXForAlignedRight(bounds.width, widget.getWidth());
                        widget.setLocation(Utils.lowerBound(x, 0), y);
                        break;
                    }
                }
                y += widget.getHeight();
            }
        }
        return widgets;
    }
}
