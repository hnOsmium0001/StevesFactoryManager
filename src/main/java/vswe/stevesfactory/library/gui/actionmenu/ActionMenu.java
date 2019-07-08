package vswe.stevesfactory.library.gui.actionmenu;

import com.google.common.base.Preconditions;
import vswe.stevesfactory.library.IWidget;
import vswe.stevesfactory.library.IWindow;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ActionMenu implements IWindow, NestedEventHandlerMixin {

    public static ActionMenu atCursor(double mouseX, double mouseY, List<? extends IEntry> entries) {
        return new ActionMenu((int) mouseX, (int) mouseY, entries) {
        };
    }

    private final Point position;
    private final List<? extends IEntry> entries;
    private IEntry focusedEntry;

    private final Dimension contents;
    private final Dimension border;

    public ActionMenu(int x, int y, List<? extends IEntry> entries) {
        this(new Point(x, y), entries);
    }

    public ActionMenu(Point position, List<? extends IEntry> entries) {
        Preconditions.checkArgument(!entries.isEmpty());

        this.position = position;
        this.entries = entries;
        this.contents = new Dimension();
        this.contents.width = entries.stream()
                .max(Comparator.comparingInt(IEntry::getWidth))
                .orElseThrow(IllegalArgumentException::new)
                .getWidth();
        this.contents.height = entries.size() * IEntry.ICON_HEIGHT;
        this.border = RenderingHelper.toBorder(contents, getBorderSize());

        int y = 0;
        for (IEntry e : entries) {
            e.onWindowChanged(this, null);
            e.setLocation(0, y);
            y += e.getHeight();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        RenderingHelper.drawRect(position, border, 94, 94, 94, 255);
        RenderingHelper.drawRect(getContentX(), getContentY(), contents, 157, 157, 157, 255);
        for (IEntry entry : entries) {
            entry.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Nullable
    @Override
    public IWidget getFocusedWidget() {
        return focusedEntry;
    }

    @Override
    public void setFocusedWidget(@Nullable IWidget widget) {
        if (widget instanceof IEntry || widget == null) {
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
    public Dimension getContentDimensions() {
        return contents;
    }

    @Override
    public List<? extends IEntry> getChildren() {
        return entries;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int getBorderSize() {
        return 1;
    }

    public void onDiscard() {
    }
}
