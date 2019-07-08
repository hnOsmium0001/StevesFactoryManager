package vswe.stevesfactory.library.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import vswe.stevesfactory.library.IWidget;
import vswe.stevesfactory.library.IWindow;
import vswe.stevesfactory.library.gui.layout.BoxSizing;
import vswe.stevesfactory.library.gui.layout.ILayoutDataProvider;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.WidgetPositionMixin;

import javax.annotation.Nullable;
import java.awt.*;

public abstract class AbstractWidget implements IWidget, ILayoutDataProvider, WidgetPositionMixin, RelocatableWidgetMixin {

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
    private boolean enabled = true;

    public AbstractWidget(int width, int height) {
        this(0, 0, width, height);
    }

    public AbstractWidget(int x, int y, int width, int height) {
        this(new Point(x, y), new Dimension(width, height));
    }

    public AbstractWidget(Point location, Dimension dimensions) {
        this.location = location;
        this.dimensions = dimensions;
    }

    @Override
    public void onParentChanged(IWidget newParent) {
        this.parent = newParent;
        this.window = newParent.getWindow();
        onParentPositionChanged();
    }

    @Override
    public void onWindowChanged(IWindow newWindow, IWidget newParent) {
        this.window = newWindow;
        this.parent = newParent;
        onParentPositionChanged();
    }

    @Override
    public void onParentPositionChanged() {
        updateAbsolutePosition();
    }

    @Override
    public void onRelativePositionChanged() {
        updateAbsolutePosition();
    }

    private void updateAbsolutePosition() {
        absX = getParentAbsXSafe() + getX();
        absY = getParentAbsYSafe() + getY();
    }

    private int getParentAbsXSafe() {
        if (parent != null) {
            return parent.getAbsoluteX();
        }
        return window.getX();
    }

    private int getParentAbsYSafe() {
        if (parent != null) {
            return parent.getAbsoluteY();
        }
        return window.getY();
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
    public boolean isInside(double x, double y) {
        return getAbsoluteX() <= x &&
                getAbsoluteXBR() > x &&
                getAbsoluteY() <= y &&
                getAbsoluteYBR() > y;
    }

    @Override
    public BoxSizing getBoxSizing() {
        return BoxSizing.BORDER_BOX;
    }

}
