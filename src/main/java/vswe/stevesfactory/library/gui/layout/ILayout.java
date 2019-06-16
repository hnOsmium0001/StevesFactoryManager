package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.widget.IWidget;

import java.awt.*;
import java.util.List;

public interface ILayout<T extends IWidget> {

    /**
     * Recalculate the positions of the given list of widgets based on a defined rule.
     *
     * @param bounds  The border that contain all widgets.
     * @param widgets A list of widgets that is to be recalculated positions. This reference may or may not get modified by this method.
     * @return A list of widgets that is recalculated by this layout handler. This does not necessarilly have to point to the same list as
     * the parameter {@code widgets}.
     */
    List<T> reflow(Dimension bounds, List<T> widgets);

    /**
     * A version of {@link #reflow(Dimension, List)} that has no side-effects on the parameters.
     * <p>
     * Implementations that do not modified the parameter {@code widgets} of {@link #reflow(Dimension, List)} should override this method
     * such that it directly delegates to the former.
     *
     * @see #reflow(Dimension, List)
     */
    default List<T> reflowPure(Dimension bounds, List<T> widgets) {
        throw new UnsupportedOperationException();
    }

}
