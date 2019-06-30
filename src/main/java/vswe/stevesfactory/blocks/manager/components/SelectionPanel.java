package vswe.stevesfactory.blocks.manager.components;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.blocks.manager.FactoryManagerGUI.TopLevelWidget;
import vswe.stevesfactory.library.collections.CompositeUnmodifiableList;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout.GrowDirection;

import java.util.*;

public class SelectionPanel extends DynamicWidthWidget<ComponentIconButton> {

    private static final StrictTableLayout<ComponentIconButton> LAYOUT = new StrictTableLayout<>(GrowDirection.DOWN, GrowDirection.RIGHT, 4);

    private final ImmutableList<ComponentIconButton> staticIcons;
    private final List<ComponentIconButton> addendumIcons;
    private final List<ComponentIconButton> icons;

    public SelectionPanel(TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupierType.MIN_WIDTH);

        ImmutableList.Builder<ComponentIconButton> icons = ImmutableList.builder();
        for (ComponentIconButton.Components value : ComponentIconButton.Components.values()) {
            icons.add(new ComponentIconButton(this, value));
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
    public List<ComponentIconButton> getChildren() {
        return icons;
    }

    @Override
    public ILayout<ComponentIconButton> getLayout() {
        return LAYOUT;
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
