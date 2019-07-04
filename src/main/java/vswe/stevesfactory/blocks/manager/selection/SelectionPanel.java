package vswe.stevesfactory.blocks.manager.selection;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.blocks.manager.FactoryManagerGUI.TopLevelWidget;
import vswe.stevesfactory.blocks.manager.components.DynamicWidthWidget;
import vswe.stevesfactory.library.collections.CompositeUnmodifiableList;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.grid.StrictTableLayout;
import vswe.stevesfactory.library.gui.layout.grid.StrictTableLayout.GrowDirection;

import java.util.*;

public class SelectionPanel extends DynamicWidthWidget<ComponentSelectionButton> {

    private static final StrictTableLayout LAYOUT = new StrictTableLayout(GrowDirection.DOWN, GrowDirection.RIGHT, 4);

    private final ImmutableList<ComponentSelectionButton> staticIcons;
    private final List<ComponentSelectionButton> addendumIcons;
    private final List<ComponentSelectionButton> icons;

    public SelectionPanel(TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupierType.MIN_WIDTH);

        ImmutableList.Builder<ComponentSelectionButton> icons = ImmutableList.builder();
        for (ComponentSelectionButton.Components value : ComponentSelectionButton.Components.values()) {
            icons.add(new ComponentSelectionButton(this, value));
        }
        this.staticIcons = icons.build();
        this.addendumIcons = new ArrayList<>();
        this.icons = CompositeUnmodifiableList.of(staticIcons, addendumIcons);

        // Reset position of the components using a table LAYOUT
        reflow();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        for (IWidget icon : staticIcons) {
            icon.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public List<ComponentSelectionButton> getChildren() {
        return icons;
    }

    @Override
    public IContainer<ComponentSelectionButton> addChildren(ComponentSelectionButton widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer<ComponentSelectionButton> addChildren(Collection<ComponentSelectionButton> widgets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reflow() {
        int w = getWidth();
        setWidth(Integer.MAX_VALUE);
        LAYOUT.reflow(getDimensions(), getChildren());
        setWidth(w);
    }

}
