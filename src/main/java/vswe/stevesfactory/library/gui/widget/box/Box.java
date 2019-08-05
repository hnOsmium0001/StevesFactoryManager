package vswe.stevesfactory.library.gui.widget.box;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

/**
 * A ready-to-use box widget for grouping widgets.
 */
public class Box<T extends IWidget & RelocatableWidgetMixin> extends AbstractContainer<T> implements ResizableWidgetMixin {

    private List<T> children = new ArrayList<>();
    private List<T> childrenView = Collections.unmodifiableList(children);

    private Consumer<List<T>> layout = l -> {
    };
    private boolean paused = false;

    public Box(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Box(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    @Override
    public void onParentPositionChanged() {
        int oldAbsX = getAbsoluteX();
        int oldAbsY = getAbsoluteY();
        super.onParentPositionChanged();
        if (getAbsoluteX() != oldAbsX || getAbsoluteY() != oldAbsY) {
            for (T child : children) {
                child.onParentPositionChanged();
            }
        }
    }

    @Override
    public List<T> getChildren() {
        return childrenView;
    }

    @Override
    public Box<T> addChildren(T widget) {
        children.add(widget);
        widget.setParentWidget(this);
        reflow();
        return this;
    }

    @Override
    public Box<T> addChildren(Collection<T> widgets) {
        children.addAll(widgets);
        widgets.forEach(widget -> widget.setParentWidget(this));
        reflow();
        return this;
    }

    @Override
    public Box<T> addChildren(Iterable<T> widgets) {
        super.addChildren(widgets);
        return this;
    }

    @Override
    public Box<T> addChildren(Iterator<T> widgets) {
        super.addChildren(widgets);
        return this;
    }

    // In most cases these are not necessary because widget's position rely on the layout

    @CanIgnoreReturnValue
    public Box<T> updateChildLocation(T child, Point point) {
        child.setLocation(point);
        reflow();
        return this;
    }

    @CanIgnoreReturnValue
    public Box<T> updateChildLocation(T child, int x, int y) {
        child.setLocation(x, y);
        reflow();
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        for (T child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    /**
     * Cancel all reflow actions until {@link #unpause()} gets triggered. This should be used as a way to avoid unnecessary reflow (layout
     * updates) when changing widget properties in batch.
     */
    @CanIgnoreReturnValue
    public Box<T> pause() {
        paused = true;
        return this;
    }

    @CanIgnoreReturnValue
    public Box<T> unpause() {
        paused = false;
        reflow();
        return this;
    }

    public boolean isPaused() {
        return paused;
    }

    @CanIgnoreReturnValue
    public Box<T> setLayout(Consumer<List<T>> layout) {
        this.layout = layout;
        return this;
    }

    @Override
    public void reflow() {
        layout.accept(children);
    }
}
