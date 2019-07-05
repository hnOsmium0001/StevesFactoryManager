package vswe.stevesfactory.library.gui.actionmenu;

import com.google.common.base.Preconditions;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.library.IWidget;
import vswe.stevesfactory.library.IWindow;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static vswe.stevesfactory.utils.RenderingHelper.fontRenderer;

public abstract class ActionMenu implements IWindow, NestedEventHandlerMixin {

    public interface IEntry extends IWidget, RelocatableWidgetMixin {

        int MARGIN_SIDES = 2;
        int ICON_WIDTH = 16;
        int ICON_HEIGHT = 16;

        /**
         * This icon must have a size of 16*16, or else action menus create undefined behaviors.
         */
        ResourceLocation getIcon();

        String getText();

        @Override
        default int getWidth() {
            return MARGIN_SIDES + ICON_WIDTH + 4 + fontRenderer().getStringWidth(getText()) + MARGIN_SIDES;
        }

        @Override
        default int getHeight() {
            return ICON_HEIGHT;
        }
    }

    public interface ILocalizedEntry extends IEntry {

        @Override
        default String getText() {
            return I18n.format(getTranslationKey());
        }

        String getTranslationKey();
    }

    private final Point position;
    private final List<? extends IEntry> entries;
    private IEntry focusedEntry;

    private final Dimension dimensions = new Dimension();
    private final Dimension border = new Dimension();

    public ActionMenu(int x, int y, List<? extends IEntry> entries) {
        this(new Point(x, y), entries);
    }

    public ActionMenu(Point position, List<? extends IEntry> entries) {
        Preconditions.checkArgument(!entries.isEmpty());

        this.position = position;
        this.entries = entries;
        this.dimensions.width = entries.stream()
                .max(Comparator.comparingInt(IEntry::getWidth))
                .orElseThrow(IllegalArgumentException::new)
                .getWidth();
        this.dimensions.height = entries.size() * IEntry.ICON_HEIGHT;
        this.border.width = dimensions.width + getBorderSize() * 2;
        this.border.height = dimensions.height + getBorderSize() * 2;

        int y = getContentY();
        for (IEntry e : entries) {
            e.onWindowChanged(this, null);
            e.setLocation(getContentX(), y);
            y += e.getHeight();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderingHelper.drawRect(position, border, 94, 94, 94, 255);
        RenderingHelper.drawRect(position, dimensions, 157, 157, 157, 255);
        for (IEntry entry : entries) {
            entry.render(mouseX, mouseY, particleTicks);
        }
    }

    @Nullable
    @Override
    public IWidget getFocusedWidget() {
        return focusedEntry;
    }

    @Override
    public void setFocusedWidget(@Nullable IWidget widget) {
        if (widget == null || widget instanceof IEntry) {
            focusedEntry.onFocusChanged(false);
            focusedEntry = (IEntry) widget;
            if (widget != null) {
                widget.onFocusChanged(true);
            }
        }
    }

    @Override
    public Dimension getBorder() {
        return border;
    }

    @Override
    public int getBorderSize() {
        return 1;
    }

    @Override
    public Dimension getContentDimensions() {
        return dimensions;
    }

    @Override
    public List<? extends IEntry> getChildren() {
        return entries;
    }

    @Override
    public Point getPosition() {
        return position;
    }
}
