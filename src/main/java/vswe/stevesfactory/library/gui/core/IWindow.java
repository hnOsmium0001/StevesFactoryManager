package vswe.stevesfactory.library.gui.core;

import net.minecraft.client.gui.INestedGuiEventHandler;
import vswe.stevesfactory.library.gui.window.IWindowPositionHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public interface IWindow<T extends IWidget> extends INestedGuiEventHandler {

    int getWidth();

    int getHeight();

    Dimension getBorder();

    Dimension getContentDimensions();

    List<IWidget> getChildren();

    void render(IWindowPositionHandler handler);

    @Nullable
    IWidget getFocusedWidget();

    /**
     * When possible, use {@link #changeFocus(IWidget, boolean)} instead.
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

    void onRemoved();

}
