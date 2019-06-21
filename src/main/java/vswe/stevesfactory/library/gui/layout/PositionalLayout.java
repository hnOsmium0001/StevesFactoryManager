package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.core.ILayout;
import vswe.stevesfactory.library.gui.core.IWidget;

import java.awt.*;
import java.util.List;

/**
 * Identity layout handler. The function is integrated in {@link IWidget}.
 */
public class PositionalLayout implements ILayout<IWidget> {

    public static final PositionalLayout INSTANCE = new PositionalLayout();

    @Override
    public List<IWidget> reflow(Dimension bounds, List<IWidget> widgets) {
        return widgets;
    }

    @Override
    public LayoutType getType() {
        return LayoutType.StatelessLayout;
    }

}
