package vswe.stevesfactory.ui.manager.components;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public abstract class DynamicWidthWidget<T extends IWidget> extends AbstractWidget implements IContainer<T>, RelocatableContainerMixin<T>, ResizableWidgetMixin, ContainerWidgetMixin<T> {

    @CanIgnoreReturnValue
    public static List<DynamicWidthWidget<?>> reflowDynamicWidth(Dimension bounds, List<DynamicWidthWidget<?>> widgets) {
        int usable = bounds.width;
        int nextX = 0;
        for (DynamicWidthWidget widget : widgets) {
            switch (widget.getWidthOccupier()) {
                case MIN_WIDTH: {
                    int w = calculateWidthMin(widget);
                    widget.setX(nextX);
                    widget.setWidth(w);
                    usable -= w;
                    nextX += w;
                    break;
                }
                case MAX_WIDTH: {
                    int w = calculateWidthMax(widget, usable);
                    widget.setX(nextX);
                    widget.setWidth(w);
                    nextX += w;
                    break;
                }
            }
        }

        return widgets;
    }

    private static int calculateWidthMin(DynamicWidthWidget<?> widget) {
        IWidget furthest = widget.getChildren().stream()
                .max(Comparator.comparingInt(IWidget::getX))
                .orElseThrow(RuntimeException::new);
        int x = furthest.getX();
        int w = furthest.getWidth();
        return x + w;
    }

    private static int calculateWidthMax(DynamicWidthWidget<?> widget, int usable) {
        return usable;
    }

    public enum WidthOccupierType {
        MIN_WIDTH, MAX_WIDTH
    }

    private WidthOccupierType widthOccupier;

    public DynamicWidthWidget(FactoryManagerGUI.TopLevelWidget parent, IWindow window, WidthOccupierType widthOccupier) {
        super(0, 0);
        onWindowChanged(window, parent);
        setX(0);
        setY(0);
        setHeight(getWindow().getContentDimensions().height);
        this.widthOccupier = widthOccupier;
    }

    public WidthOccupierType getWidthOccupier() {
        return widthOccupier;
    }
}
