package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.core.ILayout;
import vswe.stevesfactory.library.gui.core.IWidget;

import java.awt.*;
import java.util.List;

/**
 * Identity layout handler: does absolutely nothing to the inputs
 * <p>
 * This layout type will ignore the {@code box-sizing} property of the widgets.
 */
public final class IdentityLayouts {

    public static final ILayout<IWidget> PLAIN_WIDGETS = new ILayout<IWidget>() {
        @Override
        public List<IWidget> reflow(Dimension bounds, List<IWidget> widgets) {
            return widgets;
        }

        @Override
        public LayoutType getType() {
            return LayoutType.StatelessLayout;
        }
    };
    public static final ILayout UNBOUNDED = new ILayout() {
        @Override
        public List reflow(Dimension bounds, List widgets) {
            return widgets;
        }

        @Override
        public LayoutType getType() {
            return LayoutType.StatelessLayout;
        }
    };

    private IdentityLayouts() {
    }

}
