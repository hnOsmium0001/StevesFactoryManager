package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableContainerMixin;

import java.awt.*;
import java.util.Collection;

public abstract class AbstractContainer<T extends IWidget> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T> {

    public AbstractContainer(int width, int height) {
        super(width, height);
    }

    public AbstractContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public AbstractContainer(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    @Override
    public void onParentChanged(IWidget newParent) {
        super.onParentChanged(newParent);
        ContainerWidgetMixin.super.onParentChanged(newParent);
    }

    @Override
    public void onWindowChanged(IWindow newWindow, IWidget newParent) {
        super.onWindowChanged(newWindow, newParent);
        ContainerWidgetMixin.super.onWindowChanged(newWindow, newParent);
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
