package vswe.stevesfactory.library.gui.widget.mixin;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.library.IWidget;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public interface RelocatableWidgetMixin extends IWidget {

    default void setLocation(Point point) {
        setLocation(point.x, point.y);
    }

    default void setLocation(int x, int y) {
        getPosition().x = x;
        getPosition().y = y;
        onRelativePositionChanged();
    }

    default void setX(int x) {
        getPosition().x = x;
        onRelativePositionChanged();
    }

    default void setY(int y) {
        getPosition().y = y;
        onRelativePositionChanged();
    }

}
