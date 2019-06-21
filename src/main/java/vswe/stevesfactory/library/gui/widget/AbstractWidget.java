package vswe.stevesfactory.library.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.core.IWindow;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.WidgetPositionMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public abstract class AbstractWidget implements IWidget, WidgetPositionMixin, RelocatableWidgetMixin {

    public static Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    public static FontRenderer fontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    private Point location;
    private Dimension dimensions;

    private IWindow window;
    private IWidget parent;

    // Cached because this might reach all the up to the root node by recursion on getAbsoluteX/Y
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
        // Update because parent's position might have changed
        updateAbsolutePositions();
    }

    public void transferOwner(IWindow newWindow, @Nullable IWidget newParent) {
        this.window = newWindow;
        if (newParent != null) {
            transferOwner(newParent);
        }
    }

    private void updateAbsolutePositions() {
        absX = parent.getAbsoluteX() + getX();
        absY = parent.getAbsoluteY() + getY();
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
    public void setLocation(int x, int y) {
        location.x = x;
        location.y = y;
        // Update because the relative position changed
        updateAbsolutePositions();
    }

    @Override
    public void setX(int x) {
        location.x = x;
        // Update because the relative position changed
        updateAbsolutePositions();
    }

    @Override
    public void setY(int y) {
        location.y = y;
        // Update because the relative position changed
        updateAbsolutePositions();
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
    public boolean isInside(double x, double y) {
        return getAbsoluteX() <= x &&
                getAbsoluteXBR() > x &&
                getAbsoluteY() <= y &&
                getAbsoluteYBR() > y;
    }

}
