package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;

public abstract class AbstractContainer<T extends IWidget> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T> {

    public static <T extends IWidget, W extends IContainer<T> & RelocatableContainerMixin<T> & ResizableWidgetMixin> void shrinkMinContent(W widget) {
        if (widget.getChildren().isEmpty()) {
            return;
        }
        T furthestRight = widget.getChildren().stream()
                .max(Comparator.comparingInt(T::getX))
                .orElseThrow(RuntimeException::new);
        int width = furthestRight.getX() + furthestRight.getWidth();
        T furthestDown = widget.getChildren().stream()
                .max(Comparator.comparingInt(T::getY))
                .orElseThrow(RuntimeException::new);
        int height = furthestDown.getY() + furthestDown.getHeight();
        widget.setDimensions(width, height);
    }

    public static <W extends IWidget & RelocatableWidgetMixin & ResizableWidgetMixin> void fillParentContainer(W widget) {
        widget.setLocation(0, 0);
        widget.setDimensions(widget.getParentWidget().getDimensions());
    }

    public AbstractContainer(IWindow window) {
        super(window);
    }

    public AbstractContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public AbstractContainer(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    @Override
    public void setParentWidget(IWidget newParent) {
        super.setParentWidget(newParent);
        ContainerWidgetMixin.super.setParentWidget(newParent);
    }

    @Override
    public IContainer<T> addChildren(T widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer<T> addChildren(Collection<T> widgets) {
        throw new UnsupportedOperationException();
    }
}
