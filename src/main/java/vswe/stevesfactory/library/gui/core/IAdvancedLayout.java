package vswe.stevesfactory.library.gui.core;

import java.awt.*;
import java.util.List;

public interface IAdvancedLayout<T extends IWidget> extends ILayout<T> {

    /**
     * A version of {@link #reflow(Dimension, java.util.List)} that has no side-effects on the parameters.
     * <p>
     * Implementations that do not modified the parameter {@code widgets} of {@link #reflow(Dimension, java.util.List)} should override this
     * method such that it directly delegates to the former.
     *
     * @see #reflow(Dimension, java.util.List)
     */
    default java.util.List<T> reflowPure(Dimension bounds, List<T> widgets) {
        throw new UnsupportedOperationException();
    }

}
