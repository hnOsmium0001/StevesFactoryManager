package vswe.stevesfactory.library.gui.widget.scroll;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.TextField.BackgroundStyle;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.RenderingHelper;

import java.util.*;

/**
 * Code adapted from Steve's Factory Manager 2 by Vswe/gigabte101.
 */
public class WrappingListView<T extends IWidget & INamedElement & RelocatableWidgetMixin> extends AbstractWidget implements IContainer<IWidget>, ContainerWidgetMixin<IWidget>, RelocatableContainerMixin<IWidget>, ResizableWidgetMixin {

    private int itemsPerRow = 5;
    private int visibleRows = 2;
    private int viewX = 5;
    private int viewY = getSearchBoxY() + getSearchBoxHeight();
    private boolean disabledScroll;
    private boolean hasSearchBox;
    private int offset;

    // Unfortunately we can't add these into children without making the children type plain IWidget
    private TextField searchBox;
    private Arrow scrollUpArrow;
    private Arrow scrollDownArrow;
    private List<T> contents = new ArrayList<>();
    private List<IWidget> children;

    private List<T> searchResults;

    public WrappingListView(IWidget parent, boolean hasSearchBox) {
        this(parent, hasSearchBox ? "" : null);
    }

    public WrappingListView(IWidget parent, String defaultText) {
        this(parent, defaultText, Arrow.up(-1, -1), Arrow.down(-1, -1));
        scrollUpArrow.setLocation(getArrowX(), getArrowUpY());
        scrollDownArrow.setLocation(getArrowX(), getArrowDownY());
    }

    public WrappingListView(IWidget parent, String defaultText, Arrow up, Arrow down) {
        // TODO
        super(80, 80);
        onParentChanged(parent);

        // Too lazy to add text change events, just make pressing enter update search
        this.hasSearchBox = defaultText != null;
        this.searchBox = createSearchBox();
        this.searchBox.setEnabled(hasSearchBox);
        this.searchBox.setText(MoreObjects.firstNonNull(defaultText, ""));

        this.scrollUpArrow = up;
        this.scrollUpArrow.onParentChanged(this);
        this.scrollDownArrow = down;
        this.scrollDownArrow.onParentChanged(this);

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
        TextField t = new TextField(getSearchBoxX(), getSearchBoxY(), getSearchBoxWidth(), getSearchBoxHeight()) {
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

    public void setScrollingSectionX(int val) {
        viewX = val;
    }

    public int getScrollingSectionX() {
        return viewX;
    }

    public int getScrollingSectionY() {
        return viewY + 3;
    }

    private int getFirstRowY() {
//        return (viewY + offset - getScrollingSectionY()) / getItemSizeWithMargin();
        return getScrollingSectionY() - offset;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }

//        List<Point> points = getItemCoordinates();
//        for (Point point : points) {
//            if (VectorHelper.isInside((int) mouseX, (int)mouseY, point.x, point.y, ITEM_SIZE, ITEM_SIZE)) {
//                onClick(children.get(point.id), mouseX, mouseY, button);
//                break;
//            }
//        }
        // Set focused wid
        getWindow().setFocusedWidget(this);
        if (ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return false;
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
//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (getAbsoluteX() * scale), (int) (minecraft().mainWindow.getHeight() - (getAbsoluteYBR() * scale)),
                (int) (getWidth() * scale), (int) (getHeight() * scale));
        for (T child : searchResults) {
            int cy = child.getAbsoluteY();
            int sy = getScrollingSectionY();
            if (cy > sy && cy <= sy + getDisplayHeight())
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
        int max = (int) (Math.ceil(((float) children.size() / itemsPerRow)) - visibleRows) * size - (size - getItemSize());
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
        int size = getItemSizeWithMargin();
        int initialX = getScrollingSectionX();
        int x = initialX;
        int y = getFirstRowY();
        for (T child : contents) {
            child.setLocation(x, y);
            x += size;
            if (x >= getWidth()) {
                x = initialX;
                y += size;
            }
        }

//        for (int row = start; row < start + visibleRows + 1; row++) {
//            for (int col = 0; col < itemsPerRow; col++) {
//                int id = row * itemsPerRow + col;
//                if (id >= 0 && id < children.size()) {
//                    int x = getScrollingSectionX() + getItemSizeWithMargin() * col;
//                    int y = getScrollingSectionY() + row * getMargin() - offset;
//                    if (y > viewY && y + ITEM_SIZE < FlowComponent.getMenuOpenSize()) {
//                        points.add(new Point(x, y));
//                    }
//                }
//            }
//        }
    }

    public void updateScrolling() {
        boolean canScroll = children.size() > itemsPerRow * visibleRows;
        if (!canScroll) {
            offset = 0;
        }
        scrollUpArrow.setEnabled(canScroll);
        scrollDownArrow.setEnabled(canScroll);
    }

    public void setItemsPerRow(int n) {
        itemsPerRow = n;
    }

    public void setVisibleRows(int n) {
        visibleRows = n;
    }

    public void setItemUpperLimit(int n) {
        viewY = n;
    }

    public int getDisplayHeight() {
        return visibleRows * getItemSizeWithMargin();
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

//    public int getArrowSrcX() {
//        return 64;
//    }
//
//    public int getArrowSrcY() {
//        return 165;
//    }
//
//    public int getSearchBoxSrcX() {
//        return 0;
//    }
//
//    public int getSearchBoxSrcY() {
//        return 165;
//    }
//
//    public int getSearchBoxTextX() {
//        return 3;
//    }
//
//    public int getSearchBoxTextY() {
//        return 3;
//    }
//
//    public int getCursorX() {
//        return 2;
//    }
//
//    public int getCursorY() {
//        return 0;
//    }
//
//    public int getCursorZ() {
//        return 5;
//    }

    public int getArrowX() {
//        return 105;
        return 0;
    }

    public int getArrowUpY() {
        return 32;
    }

    public int getArrowDownY() {
        return 42;
    }

    public int getSearchBoxWidth() {
        return 64;
    }

    public int getSearchBoxHeight() {
        return 12;
    }

    public int getSearchBoxX() {
        return 5;
    }

    public int getSearchBoxY() {
        return 5;
    }

    public int getStatusTextX() {
        return 75;
    }

    public int getStatusTextY() {
        return 9;
    }
}