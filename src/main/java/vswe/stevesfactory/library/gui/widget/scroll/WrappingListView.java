package vswe.stevesfactory.library.gui.widget.scroll;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.TextField.BackgroundStyle;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Code adapted from Steve's Factory Manager 2 by Vswe/gigabte101.
 */
public class WrappingListView<T extends IWidget & INamedElement & RelocatableWidgetMixin> extends AbstractWidget implements IContainer<IWidget>, ContainerWidgetMixin<IWidget>, RelocatableContainerMixin<IWidget>, ResizableWidgetMixin {

    // Scrolling states
    private int offset;
    private boolean disabledScroll;
    private Rectangle contentArea = new Rectangle();

    // Search box states
    private boolean hasSearchBox;
    private List<T> searchResults;

    // Child widgets
    private TextField searchBox;
    private Arrow scrollUpArrow;
    private Arrow scrollDownArrow;
    private List<T> contents = new ArrayList<>();
    private List<IWidget> children;

    public WrappingListView(IWidget parent, boolean hasSearchBox) {
        this(parent, hasSearchBox ? "" : null);
    }

    public WrappingListView(IWidget parent, String defaultText) {
        super(80, 80);
        onParentChanged(parent);

        this.contentArea.setSize(getDimensions());

        // Too lazy to add text change events, just make pressing enter update search
        this.hasSearchBox = defaultText != null;
        this.searchBox = createSearchBox();
        this.searchBox.setEnabled(hasSearchBox);
        this.searchBox.setText(MoreObjects.firstNonNull(defaultText, ""));

        this.scrollUpArrow = Arrow.up(0, 0);
        this.scrollUpArrow.onParentChanged(this);
        this.scrollDownArrow = Arrow.down(0, 0);
        this.scrollDownArrow.onParentChanged(this);
        this.alignArrows();

        this.children = new AbstractList<IWidget>() {
            @Override
            public IWidget get(int i) {
                switch (i) {
                    case 0: return searchBox;
                    case 1: return scrollUpArrow;
                    case 2: return scrollDownArrow;
                    default: return contents.get(i - 3);
                }
            }

            @Override
            public int size() {
                return 3 + contents.size();
            }
        };
        updateSearch();
    }

    private TextField createSearchBox() {
        TextField t = new TextField(0, 0, getSearchBoxWidth(), getSearchBoxHeight()) {
            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (keyCode == GLFW.GLFW_KEY_ENTER) {
                    updateSearch();
                    return true;
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        };
        t.onParentChanged(this);
        t.setBackgroundStyle(BackgroundStyle.RED_OUTLINE);
        return t;
    }

    protected List<T> searchItems(String search) {
        List<T> result = new ArrayList<>();
        for (T child : contents) {
            if (search.equals(child.getName())) {
                result.add(child);
            }
        }
        return result;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }

        // TODO fix focus target for pressing enter
        return ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        // TODO add search status
//            if (searchBox.getText().length() > 0 || children.size() > 0) {
//                gui.drawString(Localization.ITEMS_FOUND.toString() + " " + children.size(), getStatusTextX, getStatusTextY, 0.7F, 0x404040);
//            }

        RenderingHelper.drawRect(getAbsoluteX(), getAbsoluteY(), getAbsoluteXBR(), getAbsoluteYBR(), 0xaaffffff);
        searchBox.render(mouseX, mouseY, particleTicks);
        scrollUpArrow.render(mouseX, mouseY, particleTicks);
        scrollDownArrow.render(mouseX, mouseY, particleTicks);

        double scale = minecraft().mainWindow.getGuiScaleFactor();
        int left = getAbsoluteX() + getScrollingSectionX();
        int bottom = getAbsoluteY() + getScrollingSectionY() + contentArea.height;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (left * scale), (int) (minecraft().mainWindow.getHeight() - (bottom * scale)),
                (int) (contentArea.width * scale), (int) (contentArea.height * scale));

        int sy = getScrollingSectionY();
        for (T child : searchResults) {
            int cy = child.getY();
            if (cy + child.getHeight() > sy && cy < sy + contentArea.height)
                child.render(mouseX, mouseY, particleTicks);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    // TODO figure out what is this
//    private float dt;
//
//    public void update(float particleTicks) {
//        if (clicked) {
//            particleTicks += dt;
//            int change = (int) (particleTicks * SCROLL_SPEED);
//            dt = particleTicks - (change / (float) SCROLL_SPEED);
//
//            scroll(change * dir);
//        }
//    }

    public void scroll(int change) {
        if (disabledScroll) {
            return;
        }
        offset += change / -20;
        int size = getItemSizeWithMargin();
        int min = 0;
        int max = (int) (Math.ceil(((float) children.size() / getItemsPerRow())) - getVisibleRows()) * size - (size - getItemSize());
//        scrollUpArrow.setEnabled(true);
//        scrollDownArrow.setEnabled(true);
        if (offset < min) {
            offset = min;
//            scrollUpArrow.setEnabled(false);
        } else if (offset > max) {
            offset = max;
//            scrollDownArrow.setEnabled(false);
        }

        reflow();
    }

    public void scrollUp(int change) {
        scroll(-change);
    }

    public void scrollUpUnit() {
        scroll(-getScrollSpeed());
    }

    public void scrollDown(int change) {
        scroll(change);
    }

    public void scrollDownUnit() {
        scroll(getScrollSpeed());
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    public List<T> getContents() {
        return contents;
    }

    @Override
    public WrappingListView<T> addChildren(IWidget widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WrappingListView<T> addChildren(Collection<IWidget> widgets) {
        throw new UnsupportedOperationException();
    }

    public WrappingListView<T> addElement(T widget) {
        Preconditions.checkArgument(widget.getWidth() == getItemSize() && widget.getHeight() == getItemSize());
        contents.add(widget);
        reflow();
        return this;
    }

    @Override
    public void reflow() {
        int initialX = getScrollingSectionX();
        int x = initialX;
        int y = getFirstRowY();
        for (T child : contents) {
            child.setLocation(x, y);
            x += getItemSizeWithMargin();
            if (x > contentArea.width) {
                x = initialX;
                y += getItemSizeWithMargin();
            }
        }
    }

    public void updateScrolling() {
        boolean canScroll = contents.size() > getItemsPerRow() * getVisibleRows();
        if (!canScroll) {
            offset = 0;
        }
        scrollUpArrow.setEnabled(canScroll);
        scrollDownArrow.setEnabled(canScroll);
    }

    public void updateSearch() {
        if (hasSearchBox()) {
            searchResults = searchBox.getText().isEmpty() ? contents : searchItems(searchBox.getText().toLowerCase());
        } else {
            searchResults = contents;
        }
        updateScrolling();
    }

    public void setSearchTarget(String s) {
        searchBox.setText(s);
        updateSearch();
    }

    public String getSearchTarget() {
        return searchBox.getText();
    }

    public void setDisabledScroll(boolean disabledScroll) {
        this.disabledScroll = disabledScroll;
    }

    public boolean hasSearchBox() {
        return hasSearchBox;
    }

    public Rectangle getContentArea() {
        return contentArea;
    }

    public int getScrollingSectionX() {
        return contentArea.x;
    }

    public int getScrollingSectionY() {
        return contentArea.y;
    }

    public int getScrollingSectionWidth() {
        return contentArea.width;
    }

    public int getScrollingSectionHeight() {
        return contentArea.height;
    }

    public int getItemsPerRow() {
        return (int) Math.ceil((double) contentArea.width / getItemSizeWithMargin());
    }

    public void setItemsPerRow(int itemsPerRow) {
        contentArea.width = itemsPerRow * getItemSizeWithMargin() - getMargin();
    }

    public int getVisibleRows() {
        return (int) Math.ceil((double) contentArea.height / getItemSizeWithMargin());
    }

    public void setVisibleRows(int visibleRows) {
        contentArea.height = visibleRows * getItemSizeWithMargin() - getMargin();
    }

    private int getFirstRowY() {
        return getScrollingSectionY() - offset;
    }

    public int getMargin() {
        return 4;
    }

    public int getItemSize() {
        return 16;
    }

    public int getItemSizeWithMargin() {
        return getItemSize() + getMargin();
    }

    public int getScrollSpeed() {
        return 100;
    }

    public int getSearchBoxWidth() {
        return 64;
    }

    public int getSearchBoxHeight() {
        return 12;
    }

    public int getStatusTextX() {
        return 75;
    }

    public int getStatusTextY() {
        return 9;
    }

    public Arrow getScrollUpArrow() {
        return scrollUpArrow;
    }

    public Arrow getScrollDownArrow() {
        return scrollDownArrow;
    }

    public TextField getSearchBox() {
        return searchBox;
    }

    public void placeArrows(int x, int y) {
        scrollUpArrow.setLocation(x, y);
        alignArrows();
    }

    public void alignArrows() {
        scrollDownArrow.setLocation(scrollUpArrow.getX(), scrollUpArrow.getY() + scrollUpArrow.getHeight() + getMargin());
    }
}