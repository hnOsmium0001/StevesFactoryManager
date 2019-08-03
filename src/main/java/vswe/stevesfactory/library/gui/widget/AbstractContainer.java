package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;

public abstract class AbstractContainer<T extends IWidget> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T> {

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

    public void shrinkMinContent() {
        if (getChildren().isEmpty()) {
            return;
        }
        T furthestRight = getChildren().stream()
                .max(Comparator.comparingInt(T::getX))
                .orElseThrow(RuntimeException::new);
        int width = furthestRight.getX() + furthestRight.getWidth();
        T furthestDown = getChildren().stream()
                .max(Comparator.comparingInt(T::getY))
                .orElseThrow(RuntimeException::new);
        int height = furthestDown.getY() + furthestDown.getHeight();
        setDimensions(width, height);
    }

    public void fillWindow() {
        setLocation(0, 0);
        setDimensions(getWindow().getContentDimensions());
    }
}
