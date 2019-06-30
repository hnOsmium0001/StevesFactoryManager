package vswe.stevesfactory.blocks.manager.components;

import vswe.stevesfactory.blocks.manager.FactoryManagerGUI.TopLevelWidget;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout.GrowDirection;

import java.util.*;

public class SelectionPanel extends DynamicWidthWidget<ComponentIconButton> {

    private static final StrictTableLayout<ComponentIconButton> layout = new StrictTableLayout<>(GrowDirection.DOWN, GrowDirection.RIGHT, 4);

    private void addEntry(String fileName) {
        ENTRIES.add(new ComponentIconButton(this, fileName));
    }

    private final List<ComponentIconButton> ENTRIES = new ArrayList<>();

    {
        addEntry("trigger.png");
        addEntry("item_import.png");
        addEntry("item_export.png");
        addEntry("item_condition.png");
        addEntry("flow_control.png");
        addEntry("fluid_import.png");
        addEntry("fluid_export.png");
        addEntry("fluid_condition.png");
        addEntry("redstone_emitter.png");
        addEntry("redstone_condition.png");
        addEntry("craft_item.png");
        addEntry("for_each.png");
        addEntry("group.png");
        addEntry("group_io.png");
        addEntry("camouflage.png");
        addEntry("sign_updater.png");
        addEntry("configurations.png");
    }

    public SelectionPanel(TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupingType.MIN_WIDTH);
        // Reset position of the components using a table layout
        reflow();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        for (IWidget icon : ENTRIES) {
            icon.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public List<ComponentIconButton> getChildren() {
        return ENTRIES;
    }

    @Override
    public ILayout<ComponentIconButton> getLayout() {
        return layout;
    }

    @Override
    public IContainer<ComponentIconButton> addChildren(ComponentIconButton widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer<ComponentIconButton> addChildren(Collection<ComponentIconButton> widgets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reflow() {
        int w = getWidth();
        setWidth(Integer.MAX_VALUE);
        getLayout().reflow(getDimensions(), getChildren());
        setWidth(w);
    }

}
