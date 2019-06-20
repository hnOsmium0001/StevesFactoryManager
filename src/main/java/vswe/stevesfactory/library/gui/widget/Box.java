package vswe.stevesfactory.library.gui.widget;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Box<T extends IWidget & RelocatableWidgetMixin> extends AbstractWidget implements IContainer<T>, RelocatableWidgetMixin, ResizableWidgetMixin, ContainerWidgetMixin<T> {

    private List<T> children = new ArrayList<>();
    private List<T> childrenView = Collections.unmodifiableList(children);

    private ILayout<T> layout;
    private boolean paused = false;

    public Box(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Box(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    @Override
    public List<T> getChildren() {
        return childrenView;
    }

    @CanIgnoreReturnValue
    public Box<T> addChildren(T widget) {
        children.add(widget);
        reflow();
        return this;
    }

    @CanIgnoreReturnValue
    public Box<T> addChildren(Collection<T> widgets) {
        children.addAll(widgets);
        reflow();
        return this;
    }

    @CanIgnoreReturnValue
    public Box<T> addChildren(Iterable<T> widgets) {
        return addChildren(widgets.iterator());
    }

    @CanIgnoreReturnValue
    public Box<T> addChildren(Iterator<T> widgets) {
        widgets.forEachRemaining(children::add);
        return this;
    }

    @CanIgnoreReturnValue
    @SafeVarargs
    public final Box<T> addChildren(T... widgets) {
        children.addAll(Arrays.asList(widgets));
        reflow();
        return this;
    }

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
    public ILayout<T> getLayout() {
        return layout;
    }

    public void setLayout(ILayout<T> layout) {
        this.layout = layout;
        reflow();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        for (T child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
    }

    /**
     * Cancel all reflow actions until {@link #unpause()} gets triggered. This should be used as a way to avoid unnecessary reflow (layout
     * updates) when changing widget properties in batch.d
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

    private void reflow() {
        if (!paused && layout != null) {
            getLayout().reflow(getDimensions(), children);
        }
    }

}
