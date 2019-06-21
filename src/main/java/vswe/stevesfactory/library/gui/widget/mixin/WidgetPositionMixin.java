package vswe.stevesfactory.library.gui.widget.mixin;

import vswe.stevesfactory.library.gui.core.IWidget;

public interface WidgetPositionMixin extends IWidget {

    @Override
    default int getX() {
        return getPosition().x;
    }

    @Override
    default int getY() {
        return getPosition().y;
    }

    @Override
    default int getWidth() {
        return getDimensions().width;
    }

    @Override
    default int getHeight() {
        return getDimensions().height;
    }

}
