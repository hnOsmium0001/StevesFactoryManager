package vswe.stevesfactory.blocks.manager.components;

import vswe.stevesfactory.blocks.manager.FactoryManagerGUI;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWindow;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;

import java.util.*;

public final class EditorPanel extends DynamicWidthWidget<FlowComponent> {

    private Set<FlowComponent> children = new HashSet<>();
    private Collection<FlowComponent> childrenView = Collections.unmodifiableCollection(children);

    public EditorPanel(FactoryManagerGUI.TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupierType.MAX_WIDTH);
    }

    @Override
    public Collection<FlowComponent> getChildren() {
        return childrenView;
    }

    @Override
    public IContainer<FlowComponent> addChildren(FlowComponent widget) {
        children.add(widget);
        return this;
    }

    @Override
    public IContainer<FlowComponent> addChildren(Collection<FlowComponent> widgets) {
        children.addAll(widgets);
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        super.render(mouseX, mouseY, particleTicks);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public void reflow() {
    }

    public void removeFlowComponent(FlowComponent flowComponent) {
        children.remove(flowComponent);
    }
}
