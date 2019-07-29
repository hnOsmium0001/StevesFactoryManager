package vswe.stevesfactory.library.gui.widget.scroll;

import com.google.common.base.MoreObjects;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.TextField.BackgroundStyle;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.util.*;

import static vswe.stevesfactory.ui.manager.FactoryManagerGUI.DOWN_RIGHT_4_STRICT_TABLE;

public abstract class ScrollController<T extends IWidget & INamedElement & RelocatableWidgetMixin> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T>, ResizableWidgetMixin {

    private int itemsPerRow = 5;
    private int visibleRows = 2;
    private int startX = 5;
    private int scrollingUpperLimit = getSearchBoxY() + getSearchBoxHeight();
    private boolean disabledScroll;

    private int offset;
    private boolean hasSearchBox;

    // Unfortunately we can't add these into children without making the children type plain IWidget
    private TextField searchBox;
    private ScrollArrow scrollUpArrow;
    private ScrollArrow scrollDownArrow;
    private List<T> children;

    private List<T> searchResults;

    public ScrollController(boolean hasSearchBox) {
        this(hasSearchBox ? "" : null);
    }

    public ScrollController(String defaultText) {
        this(defaultText, ScrollArrow.up(-1, -1), ScrollArrow.down(-1, -1));
        scrollUpArrow.setLocation(getArrowX(), getArrowUpY());
        scrollDownArrow.setLocation(getArrowX(), getArrowDownY());
    }

    public ScrollController(String defaultText, ScrollArrow up, ScrollArrow down) {
        // TODO
        super(0, 0);

        // Too lazy to add text change events, just make pressing enter update search
        this.hasSearchBox = defaultText != null;
        this.searchBox = new TextField(getSearchBoxX(), getSearchBoxY(), getSearchBoxWidth(), getSearchBoxHeight()).setBackgroundStyle(BackgroundStyle.RED_OUTLINE);
        this.searchBox.setEnabled(hasSearchBox);
        this.searchBox.setText(MoreObjects.firstNonNull(defaultText, ""));

        this.scrollUpArrow = up;
        this.scrollUpArrow.onParentChanged(this);
        this.scrollDownArrow = down;
        this.scrollDownArrow.onParentChanged(this);
        updateSearch();
    }

    protected List<T> searchItems(String search) {
        List<T> result = new ArrayList<>();
        for (T child : children) {
            if (search.equals(child.getName())) {
                result.add(child);
            }
        }
        return result;
    }

    public void setX(int val) {
        startX = val;
    }

    public int getScrollingStartX() {
        return startX;
    }

    public int getScrollingStartY() {
        return scrollingUpperLimit + 3;
    }

    private int getFirstRow() {
        return (scrollingUpperLimit + offset - getScrollingStartY()) / getItemSizeWithMargin();
    }

//    private List<Point> getItemCoordinates() {
//        List<Point> points = new ArrayList<>();
//
//        int start = getFirstRow();
//        for (int row = start; row < start + visibleRows + 1; row++) {
//            for (int col = 0; col < itemsPerRow; col++) {
//                int id = row * itemsPerRow + col;
//                if (id >= 0 && id < children.size()) {
//                    int x = getScrollingStartX() + ITEM_SIZE_WITH_MARGIN * col;
//                    int y = getScrollingStartY() + row * ITEM_SIZE_WITH_MARGIN - offset;
//                    if (y > scrollingUpperLimit && y + ITEM_SIZE < FlowComponent.getMenuOpenSize()) {
//                    points.add(new Point(x, y));
//                    }
//                }
//            }
//        }
//        return points;
//    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInside(mouseX, mouseY)) {
            return false;
        }

//        List<Point> points = getItemCoordinates();
//        for (Point point : points) {
//            if (VectorHelper.isInside((int) mouseX, (int)mouseY, point.x, point.y, ITEM_SIZE, ITEM_SIZE)) {
//                onClick(children.get(point.id), mouseX, mouseY, button);
//                break;
//            }
//        }
        ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button);
        searchBox.mouseClicked(mouseX, mouseY, button);
        scrollUpArrow.mouseClicked(mouseX, mouseY, button);
        scrollDownArrow.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        searchBox.render(mouseX, mouseY, particleTicks);
        // TODO add search status
//            if (searchBox.getText().length() > 0 || children.size() > 0) {
//                gui.drawString(Localization.ITEMS_FOUND.toString() + " " + children.size(), getStatusTextX, getStatusTextY, 0.7F, 0x404040);
//            }

        scrollUpArrow.render(mouseX, mouseY, particleTicks);
        scrollDownArrow.render(mouseX, mouseY, particleTicks);
        // TODO glScissor rule
        for (T child : searchResults) {
            int cy = child.getAbsoluteY();
            int sy = getScrollingStartY();
            if (cy > sy && cy <= sy + getDisplayHeight())
                child.render(mouseX, mouseY, particleTicks);
        }
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
        scrollUpArrow.setEnabled(true);
        scrollDownArrow.setEnabled(true);
        if (offset < min) {
            offset = min;
            scrollUpArrow.setEnabled(false);
        } else if (offset > max) {
            offset = max;
            scrollDownArrow.setEnabled(false);
        }
    }

    public void scrollUp(int change) {
        scroll(change);
    }

    public void scrollUpUnit() {
        scroll(getScrollSpeed());
    }

    public void scrollDown(int change) {
        scroll(-change);
    }

    public void scrollDownUnit() {
        scroll(-getScrollSpeed());
    }

    @Override
    public List<T> getChildren() {
        return children;
    }

    @Override
    public ScrollController<T> addChildren(T widget) {
        children.add(widget);
        return this;
    }

    @Override
    public ScrollController<T> addChildren(Collection<T> widgets) {
        children.addAll(widgets);
        return this;
    }

    @Override
    public void reflow() {
        DOWN_RIGHT_4_STRICT_TABLE.reflow(getDimensions(), children);
        int top = getFirstRow();
        for (T child : children) {
            child.setY(top + offset);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            updateSearch();
            return true;
        }
        return false;
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
        scrollingUpperLimit = n;
    }

    public int getDisplayHeight() {
        return visibleRows * getItemSizeWithMargin();
    }

    public void updateSearch() {
        if (hasSearchBox()) {
            searchResults = searchBox.getText().isEmpty() ? children : searchItems(searchBox.getText().toLowerCase());
        } else {
            searchResults = children;
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
        return 105;
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