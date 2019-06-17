package vswe.stevesfactory.library.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.core.IWindow;

import javax.annotation.Nullable;
import java.awt.*;

public abstract class AbstractWidget implements IWidget {

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    private Point location;

    private Dimension dimensions;
    private IWidget parent;

    private IWindow window;

    private boolean enabled;

    public AbstractWidget(int x, int y, int width, int height) {
        this(new Point(x, y), new Dimension(width, height));
    }

    public AbstractWidget(Point location, Dimension dimensions) {
        this.location = location;
        this.dimensions = dimensions;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    @Override
    public Dimension getDimensions() {
        return dimensions;
    }

    public int getWidth() {
        return dimensions.width;
    }

    public int getHeight() {
        return dimensions.height;
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

}
