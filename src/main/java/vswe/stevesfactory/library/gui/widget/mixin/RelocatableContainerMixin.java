package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;

public interface RelocatableContainerMixin<T extends IWidget> extends IContainer<T>, RelocatableWidgetMixin {

    default void notifyChildrenForPositionChange() {
        // Prevent NPE when containers setting coordinates before child widgets get initialized
        if (getChildren() != null) {
            for (T child : getChildren()) {
                child.onParentPositionChanged();
            }
        }
    }

    @Override
    default void onParentPositionChanged() {
        notifyChildrenForPositionChange();
    }

    @Override
    default void setLocation(int x, int y) {
        RelocatableWidgetMixin.super.setLocation(x, y);
        notifyChildrenForPositionChange();
    }

    @Override
    default void setX(int x) {
        RelocatableWidgetMixin.super.setX(x);
        notifyChildrenForPositionChange();
    }

    @Override
    default void setY(int y) {
        RelocatableWidgetMixin.super.setY(y);
        notifyChildrenForPositionChange();
    }
}
