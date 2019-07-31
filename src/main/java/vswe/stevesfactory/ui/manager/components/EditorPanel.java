package vswe.stevesfactory.ui.manager.components;

import mcp.MethodsReturnNonnullByDefault;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.Icon;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableContainerMixin;
import vswe.stevesfactory.library.gui.widget.scroll.WrappingListView;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.components.ControlFlowNodes.Node;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class EditorPanel extends DynamicWidthWidget<FlowComponent> implements ContainerWidgetMixin<FlowComponent>, RelocatableContainerMixin<FlowComponent> {

    /**
     * This is a tree set (ordered set) because handling z-index of the flow components need things to be sorted.
     */
    private TreeSet<FlowComponent> children = new TreeSet<>();
    private Collection<FlowComponent> childrenView = new DescendingTreeSetBackedUnmodifiableCollection<>(children);
    private int nextZIndex = 0;
    private int nextID = 0;

    // Node connection state
    private Node selectedNode;

    private WrappingListView<Icon> f;

    public EditorPanel(FactoryManagerGUI.TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupierType.MAX_WIDTH);
        f = new WrappingListView<>(this, false);
        f.setDimensions(160, 100);
        f.onParentChanged(this);
        TextureWrapper t = TextureWrapper.ofFlowComponent(0, 0, 16, 16);
        for (int i = 0; i < 64; i++) {
            int fi = i + 1;
            f.addElement(new Icon(f, 0, 0, t) {
                @Override
                public void render(int mouseX, int mouseY, float particleTicks) {
                    super.render(mouseX, mouseY, particleTicks);
                    RenderingHelper.drawTextCenteredVertically(String.valueOf(fi), getAbsoluteX(), getAbsoluteY(), getAbsoluteYBR(), 0xffffff);
                }
            });
        }
        f.placeArrows(105, 32);
        f.setItemsPerRow(5);
        f.setVisibleRows(4);
        f.getContentArea().translate(0, 20);
        f.reflow();
    }

    @Override
    public Collection<FlowComponent> getChildren() {
        return childrenView;
    }

    @Override
    public IContainer<FlowComponent> addChildren(FlowComponent widget) {
        widget.onParentChanged(this);
        widget.setZIndex(nextZIndex());
        children.add(widget);
        return this;
    }

    @Override
    public IContainer<FlowComponent> addChildren(Collection<FlowComponent> widgets) {
        for (FlowComponent widget : widgets) {
            widget.onParentChanged(this);
            widget.setZIndex(nextZIndex());
        }
        children.addAll(widgets);
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        if (selectedNode != null) {
            Node.drawConnectionLine(selectedNode, mouseX, mouseY);
        }
        f.render(mouseX, mouseY, particleTicks);

        // Iterate in ascending order for rendering as a special case
        for (FlowComponent child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Cancel node selection
        if (selectedNode != null && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            selectedNode = null;
            return true;
        }
        f.mouseClicked(mouseX, mouseY, button);

        // All other events will be iterated in descending order
        for (FlowComponent child : getChildren()) {
            // We know all child widgets are FlowComponent's, which are containers, therefore we can safely ignore whether the mouse is in box or not
            if (child.mouseClicked(mouseX, mouseY, button)) {
                raiseComponentToTop(child);
                return true;
            }
        }
        if (isInside(mouseX, mouseY)) {
            getWindow().setFocusedWidget(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        f.mouseReleased(mouseX, mouseY, button);
        return ContainerWidgetMixin.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        f.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return ContainerWidgetMixin.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        f.mouseScrolled(mouseX, mouseY, scroll);
        return ContainerWidgetMixin.super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        f.keyPressed(keyCode, scanCode, modifiers);
        return ContainerWidgetMixin.super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        f.keyReleased(keyCode, scanCode, modifiers);
        return ContainerWidgetMixin.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        f.charTyped(charTyped, keyCode);
        return ContainerWidgetMixin.super.charTyped(charTyped, keyCode);
    }

    @Override
    public void update(float particleTicks) {
        f.update(particleTicks);
        ContainerWidgetMixin.super.update(particleTicks);
    }

    @Override
    public void reflow() {
    }

    @Override
    public void setX(int x) {
        RelocatableContainerMixin.super.setX(x);
        if (f != null) f.setLocation(40, 0);
    }

    @Override
    public void setY(int y) {
        RelocatableContainerMixin.super.setY(y);
        if (f != null) f.setLocation(40, 0);
    }

    public void removeFlowComponent(FlowComponent flowComponent) {
        children.remove(flowComponent);
    }

    private void raiseComponentToTop(FlowComponent target) {
        // Move the flow component to top by setting its z-index to the maximum z-index ever given out
        target.setZIndex(nextZIndex());

        updateChild(target);
    }

    private int nextZIndex() {
        return nextZIndex++;
    }

    private int getLastDistributedZIndex() {
        return nextZIndex - 1;
    }

    private void updateChild(FlowComponent child) {
        children.remove(child);
        children.add(child);
    }

    int nextID() {
        return nextID++;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Node selection logic
    ///////////////////////////////////////////////////////////////////////////

    public void startConnection(Node source) {
        selectedNode = source;
    }

    public boolean tryFinishConnection(Node target) {
        if (selectedNode != null && selectedNode.shouldConnect(target) && target.shouldConnect(selectedNode)) {
            target.connect(selectedNode);
            selectedNode = null;
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Node selection logic end
    ///////////////////////////////////////////////////////////////////////////

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    private static class DescendingTreeSetBackedUnmodifiableCollection<E> extends AbstractCollection<E> {

        private final TreeSet<E> s;

        public DescendingTreeSetBackedUnmodifiableCollection(TreeSet<E> s) {
            this.s = s;
        }

        public int size() {
            return this.s.size();
        }

        public boolean isEmpty() {
            return this.s.isEmpty();
        }

        public boolean contains(Object var1) {
            return this.s.contains(var1);
        }

        public Object[] toArray() {
            return this.s.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return this.s.toArray(a);
        }

        public String toString() {
            return this.s.toString();
        }

        @Override
        public Iterator<E> iterator() {
            return s.descendingIterator();
        }

        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> c) {
            return this.s.containsAll(c);
        }

        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public void forEach(Consumer<? super E> c) {
            this.s.forEach(c);
        }

        public boolean removeIf(Predicate<? super E> p) {
            throw new UnsupportedOperationException();
        }

        public Spliterator<E> spliterator() {
            return this.s.spliterator();
        }

        public Stream<E> stream() {
            return this.s.stream();
        }

        public Stream<E> parallelStream() {
            return this.s.parallelStream();
        }
    }
}
