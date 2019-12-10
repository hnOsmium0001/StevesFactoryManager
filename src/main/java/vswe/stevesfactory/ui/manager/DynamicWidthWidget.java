package vswe.stevesfactory.ui.manager;

import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public abstract class DynamicWidthWidget<T extends IWidget> extends AbstractContainer<T> implements ResizableWidgetMixin {

    @SuppressWarnings("UnusedReturnValue")
    public static List<DynamicWidthWidget<?>> reflowDynamicWidth(Dimension bounds, List<DynamicWidthWidget<?>> widgets) {
        int amountMaxWidth = 0;
        for (DynamicWidthWidget<?> widget : widgets) {
            switch (widget.getWidthOccupier()) {
                case MIN_WIDTH:
                    break;
                case MAX_WIDTH:
                    amountMaxWidth++;
                    break;
            }
        }

        int usable = bounds.width;
        for (DynamicWidthWidget widget : widgets) {
            if (widget.getWidthOccupier() == WidthOccupierType.MIN_WIDTH) {
                usable -= widget.getWidth();
            }
        }

        int unit = usable / amountMaxWidth;
        int nextX = 0;
        for (DynamicWidthWidget<?> widget : widgets) {
            if (widget.getWidthOccupier() == WidthOccupierType.MAX_WIDTH) {
                widget.setWidth(unit);
            }
            widget.setX(nextX);
            widget.onAfterReflow();
            nextX += widget.getWidth();
        }

        return widgets;
    }

    public enum WidthOccupierType {
        MIN_WIDTH, MAX_WIDTH
    }

    private WidthOccupierType widthOccupier;

    public DynamicWidthWidget(WidthOccupierType widthOccupier) {
        super(0, 0, 0, 0);
        this.widthOccupier = widthOccupier;
    }

    @Override
    public void onParentPositionChanged() {
        super.onParentPositionChanged();
        setHeight(getParentHeight());
    }

    public WidthOccupierType getWidthOccupier() {
        return widthOccupier;
    }

    protected void onAfterReflow() {
    }
}
