package vswe.stevesfactory.library.gui.core;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public interface IWindow<T extends IWidget> {

    Dimension getBorder();

    Dimension getContentDimensions();

    List<IWidget> getChildren();

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

    boolean isMouseOver(double mouseX, double mouseY);

    void mouseClicked(double mouseX, double mouseY, int button);

    void mouseReleased(double mouseX, double mouseY, int button);

    void mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY);

    void mouseScrolled(double mouseX, double mouseY, double amountScrolled);

    void keyPressed(int keyCode, int scanCode, int modifiers);

    void keyReleased(int keyCode, int scanCode, int modifiers);

    void charTyped(char charTyped, int keyCode);

}
