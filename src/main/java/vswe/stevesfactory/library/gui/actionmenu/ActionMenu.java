package vswe.stevesfactory.library.gui.actionmenu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import jdk.nashorn.internal.ir.annotations.Immutable;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.window.DiscardCondition;
import vswe.stevesfactory.library.gui.window.IPopupWindow;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ActionMenu implements IPopupWindow, NestedEventHandlerMixin {

    public static ActionMenu atCursor(double mouseX, double mouseY, List<? extends IEntry> entries) {
        return new ActionMenu((int) mouseX, (int) mouseY, entries);
    }

    private final Point position;
    private final List<? extends IEntry> entries;
    private final List<? extends Section> sections;
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
        this.contents.height = entries.stream()
                .mapToInt(IEntry::getHeight)
                .sum();
        this.border = RenderingHelper.toBorder(contents, getBorderSize());

        Section section = new Section();
        // Safe downwards erasure cast
        @SuppressWarnings("unchecked") List<IEntry> c = (List<IEntry>) entries;
        section.addChildren(c);
        this.sections = ImmutableList.of(section);

        int y = 0;
        for (IEntry e : entries) {
            e.attach(this);
            e.setLocation(0, y);
            y += e.getHeight();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        RenderingHelper.drawRect(position, border, 75, 75, 75, 255);
        RenderingHelper.drawRect(getContentX(), getContentY(), contents, 61, 61, 61, 255);
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
            if (focusedEntry != null) {
                focusedEntry.onFocusChanged(false);
            }
            focusedEntry = (IEntry) widget;
            if (widget != null) {
                widget.onFocusChanged(true);
            }
        }
    }

    public boolean isLastSection(Section section) {
        return Iterables.getLast(sections) == section;
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

    @Override
    public int getLifespan() {
        return -1;
    }

    @Override
    public boolean shouldDrag(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public DiscardCondition getDiscardCondition() {
        return DiscardCondition.UNFOCUSED_CLICK;
    }
}
