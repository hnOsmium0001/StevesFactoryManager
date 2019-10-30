package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.widget.Checkbox;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.ui.manager.editor.Menu;
import vswe.stevesfactory.utils.Utils;

import java.awt.*;
import java.util.List;
import java.util.Map;

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

    public static <T, W extends IWidget> void reflow(int initialX, int initialY, int width, Map<T, W> widgets) {
        int x = initialX;
        int y = Menu.HEADING_BOX.getPortionHeight() + initialY;
        int i = 1;
        for (Map.Entry<T, W> entry : widgets.entrySet()) {
            W widget = entry.getValue();
            widget.setLocation(x, y);
            if (i % 2 == 0) {
                x = initialX;
                y += 10;
            } else {
                x = width / 2;
            }
            i++;
        }
    }
}
