package vswe.stevesfactory.library.gui.widget;

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

    void setX(int x);

    void setY(int y);

}
