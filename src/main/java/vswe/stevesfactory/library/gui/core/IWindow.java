package vswe.stevesfactory.library.gui.core;

import net.minecraft.client.gui.IGuiEventListener;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public interface IWindow<T extends IWidget> extends IGuiEventListener {

    Dimension getBorder();

    Dimension getContentDimensions();

    List<IWidget> getChildren();

    @Nullable
    IWidget getFocusedWidget();

    /**
     * When possible, use {@link #changeFocus(IWidget, boolean)} instead.
     * @param widget
     */
    void setFocusedWidget(@Nullable IWidget widget);

    default boolean changeFocus(IWidget widget, boolean focus) {
        if (focus && widget.isEnabled()) {
            setFocusedWidget(widget);
            return true;
        } else {
            setFocusedWidget(null);
            return false;
        }
    }

    ILayout<T> getLayout();

    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }

    // ============================ //
    // Disabled methods from parent //
    // ============================ //

    @Override
    default boolean changeFocus(boolean focus) {
        throw new UnsupportedOperationException();
    }

}
