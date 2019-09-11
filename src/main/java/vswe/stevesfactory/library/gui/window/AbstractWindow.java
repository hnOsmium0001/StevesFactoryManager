package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.IWindow;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.Inspections.IInspectionInfoProvider;

import javax.annotation.Nullable;
import java.awt.*;

import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledHeight;
import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledWidth;

public abstract class AbstractWindow implements IWindow, IInspectionInfoProvider, NestedEventHandlerMixin {

    public final Point position;
    public final Dimension contents;
    public final Dimension border;

    private IWidget focusedWidget;

    public AbstractWindow() {
        this.position = new Point();
        this.contents = new Dimension();
        this.border = new Dimension();
    }

    @Override
    public Dimension getBorder() {
        return border;
    }

    @Override
    public Dimension getContentDimensions() {
        return contents;
    }

    public void setContents(int width, int height) {
        contents.width = width;
        contents.height = height;
        int borderSize = getBorderSize();
        border.width = borderSize + width + borderSize;
        border.height = borderSize + height + borderSize;
    }

    public void setBorder(int width, int height) {
        border.width = width;
        border.height = height;
        int borderSize = getBorderSize();
        contents.width = width - borderSize * 2;
        contents.height = height - borderSize * 2;
    }

    public void centralize() {
        position.x = scaledWidth() / 2 - getWidth() / 2;
        position.y = scaledHeight() / 2 - getHeight() / 2;
        updatePosition();
    }

    @Override
    public void setPosition(int x, int y) {
        NestedEventHandlerMixin.super.setPosition(x, y);
        updatePosition();
    }

    protected final void updatePosition() {
        for (IWidget child : getChildren()) {
            child.onParentPositionChanged();
        }
    }

    protected final void renderChildren(int mouseX, int mouseY, float particleTicks) {
        for (IWidget child : getChildren()) {
            child.render(mouseX, mouseY, particleTicks);
        }
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Nullable
    @Override
    public IWidget getFocusedWidget() {
        return focusedWidget;
    }

    @Override
    public void setFocusedWidget(@Nullable IWidget widget) {
        focusedWidget = widget;
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        receiver.line(this.toString());
        receiver.line("Position=" + this.getPosition());
        receiver.line("Dimensions=" + this.getBorder());
        receiver.line("BorderSize=" + this.getBorderSize());
        receiver.line("ContentDimensions=" + this.getContentDimensions());
        receiver.line("ContentX=" + this.getContentX());
        receiver.line("ContentY=" + this.getContentY());
    }
}
