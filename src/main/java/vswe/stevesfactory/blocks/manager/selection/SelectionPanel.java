package vswe.stevesfactory.blocks.manager.selection;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.blocks.manager.FactoryManagerGUI.TopLevelWidget;
import vswe.stevesfactory.blocks.manager.components.DynamicWidthWidget;
import vswe.stevesfactory.library.collections.CompositeUnmodifiableList;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout.GrowDirection;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;

import javax.annotation.Nonnull;
import java.util.*;

public final class SelectionPanel extends DynamicWidthWidget<ComponentSelectionButton> implements ContainerWidgetMixin<ComponentSelectionButton> {

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

    @Nonnull
    @Override
    public TopLevelWidget getParentWidget() {
        return Objects.requireNonNull((TopLevelWidget) super.getParentWidget());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (isInside(mouseX, mouseY)) {
            getWindow().setFocusedWidget(this);
            return true;
        }
        return false;
    }
}
