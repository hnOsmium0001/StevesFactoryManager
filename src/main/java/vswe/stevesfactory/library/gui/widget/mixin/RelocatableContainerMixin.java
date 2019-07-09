package vswe.stevesfactory.library.gui.widget.mixin;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;

import java.util.Collection;

public interface RelocatableContainerMixin<T extends IWidget> extends IContainer<T>, RelocatableWidgetMixin {

    default void notifyChildrenForPositionChange() {
        // Prevent NPE when containers setting coordinates before child widgets get initialized
        Collection<T> children = MoreObjects.firstNonNull(getChildren(), ImmutableList.of());
        for (T child : children) {
            child.onParentPositionChanged();
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
