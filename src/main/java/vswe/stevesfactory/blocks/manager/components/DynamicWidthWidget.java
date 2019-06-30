package vswe.stevesfactory.blocks.manager.components;

import vswe.stevesfactory.blocks.manager.FactoryManagerGUI;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.*;

public abstract class DynamicWidthWidget<T extends IWidget> extends AbstractWidget implements IContainer<T>, RelocatableWidgetMixin, ResizableWidgetMixin, ContainerWidgetMixin<T> {

    public enum WidthOccupingType {
        MIN_WIDTH, MAX_WIDTH
    }

    private WidthOccupingType widthOccupier;

    public DynamicWidthWidget(FactoryManagerGUI.TopLevelWidget parent, IWindow window, WidthOccupingType widthOccupier) {
        super(0, 0);
        onWindowChanged(window, parent);
        setX(0);
        setY(0);
        setHeight(getWindow().getContentDimensions().height);
        this.widthOccupier = widthOccupier;
    }

    public WidthOccupingType getWidthOccupier() {
        return widthOccupier;
    }

}
