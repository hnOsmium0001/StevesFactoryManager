package vswe.stevesfactory.library.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.core.IWindow;
import vswe.stevesfactory.library.gui.widget.mixin.WidgetPositionMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public abstract class AbstractWidget implements IWidget, WidgetPositionMixin {

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    private Point location;
    private Dimension dimensions;

    private IWindow window;
    private IWidget parent;

    private int absX;
    private int absY;

    private boolean enabled;

    public AbstractWidget(int x, int y, int width, int height) {
        this(new Point(x, y), new Dimension(width, height));
    }

    public AbstractWidget(Point location, Dimension dimensions) {
        this.location = location;
        this.dimensions = dimensions;
    }

    public void transferOwner(@Nonnull IWidget newParent) {
        this.parent = newParent;
        this.absX = newParent.getAbsoluteX() + getX();
        this.absY = newParent.getAbsoluteY() + getY();
    }

    private void transferOwner(IWindow newWindow, @Nullable IWidget newParent) {
        this.window = newWindow;
        if (newParent != null) {
            transferOwner(newParent);
        }
    }

    @Override
    public Point getPosition() {
        return location;
    }

    @Override
    public int getAbsoluteX() {
        return absX;
    }

    @Override
    public int getAbsoluteY() {
        return absY;
    }

    public int getAbsoluteXBR() {
        return getAbsoluteX() + getWidth();
    }

    public int getAbsoluteYBR() {
        return getAbsoluteY() + getHeight();
    }

    @Override
    public Dimension getDimensions() {
        return dimensions;
    }


    @Nullable
    @Override
    public IWidget getParentWidget() {
        return parent;
    }

    @Override
    public IWindow getWindow() {
        return window;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isInside(double mouseX, double mouseY) {
        return getAbsoluteX() <= mouseX &&
                getAbsoluteXBR() > mouseX &&
                getAbsoluteY() <= mouseY &&
                getAbsoluteYBR() > mouseY;
    }

}
