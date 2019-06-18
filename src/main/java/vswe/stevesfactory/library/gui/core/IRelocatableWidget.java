package vswe.stevesfactory.library.gui.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public interface IRelocatableWidget extends IWidget {

    default void setLocation(Point point) {
        setLocation(point.x, point.y);
    }

    default void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }

    default void setX(int x) {
        getPosition().x = x;
    }

    default void setY(int y) {
        getPosition().y = y;
    }

}
