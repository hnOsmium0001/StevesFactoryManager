package vswe.stevesfactory.library.gui.widget.scroll;

import com.google.common.base.MoreObjects;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.TextField.BackgroundStyle;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import static vswe.stevesfactory.ui.manager.FactoryManagerGUI.DOWN_RIGHT_4_STRICT_TABLE;

public abstract class ScrollController<T extends IWidget & INamedElement & RelocatableWidgetMixin> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T>, ResizableWidgetMixin {

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SIZE_WITH_MARGIN = 20;

    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_HEIGHT = 6;
    private static final int ARROW_SRC_X = 64;
    private static final int ARROW_SRC_Y = 165;
    private static final int ARROW_X = 105;
    private static final int ARROW_Y_UP = 32;
    private static final int ARROW_Y_DOWN = 42;

    private static final int SEARCH_BOX_WIDTH = 64;
    private static final int SEARCH_BOX_HEIGHT = 12;
    private static final int SEARCH_BOX_SRC_X = 0;
    private static final int SEARCH_BOX_SRC_Y = 165;
    private static final int SEARCH_BOX_X = 5;
    private static final int SEARCH_BOX_Y = 5;
    private static final int SEARCH_BOX_TEXT_X = 3;
    private static final int SEARCH_BOX_TEXT_Y = 3;
    private static final int CURSOR_X = 2;
    private static final int CURSOR_Y = 0;
    private static final int CURSOR_Z = 5;
    private static final int AMOUNT_TEXT_X = 75;
    private static final int AMOUNT_TEXT_Y = 9;

    private static final int SCROLL_SPEED = 100;

    private int itemsPerRow = 5;
    private int visibleRows = 2;
    private int startX = 5;
    private int scrollingUpperLimit = SEARCH_BOX_Y + SEARCH_BOX_HEIGHT;
    private boolean disabledScroll;

    private int offset;
    private int dir;
    private boolean clicked;
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
        // TODO
        super(0, 0);
//        if (defaultText != null) {
        // TODO
//            searchBox = new TextBoxLogic(Integer.MAX_VALUE, SEARCH_BOX_WIDTH - SEARCH_BOX_TEXT_X * 2)
//            {
//                @Override
//                protected void textChanged()
//                {
//                    if (getText().length() > 0)
//                    {
//                        searchItems();
//                    } else
//                    {
//                        children.clear();
//                        updateScrolling();
//                    }
//                }
//            };
//        }

        this.hasSearchBox = defaultText != null;
        this.searchBox = new TextField(0, 0, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT).setBackgroundStyle(BackgroundStyle.RED_OUTLINE);
        this.searchBox.setEnabled(hasSearchBox);
        this.searchBox.setText(MoreObjects.firstNonNull(defaultText, ""));

        this.scrollUpArrow = ScrollArrow.up(this, ARROW_X, ARROW_Y_UP);
        this.scrollDownArrow = ScrollArrow.down(this, ARROW_X, ARROW_Y_DOWN);
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
        return (scrollingUpperLimit + offset - getScrollingStartY()) / ITEM_SIZE_WITH_MARGIN;
    }

    // TODO reflow
    private List<Point> getItemCoordinates() {
        List<Point> points = new ArrayList<>();

        int start = getFirstRow();
        for (int row = start; row < start + visibleRows + 1; row++) {
            for (int col = 0; col < itemsPerRow; col++) {
                int id = row * itemsPerRow + col;
                if (id >= 0 && id < children.size()) {
                    int x = getScrollingStartX() + ITEM_SIZE_WITH_MARGIN * col;
                    int y = getScrollingStartY() + row * ITEM_SIZE_WITH_MARGIN - offset;
                    // TODO
//                    if (y > scrollingUpperLimit && y + ITEM_SIZE < FlowComponent.getMenuOpenSize()) {
                    points.add(new Point(x, y));
//                    }
                }
            }
        }

        return points;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button);
//        List<Point> points = getItemCoordinates();
//        for (Point point : points) {
//            if (VectorHelper.isInside((int) mouseX, (int)mouseY, point.x, point.y, ITEM_SIZE, ITEM_SIZE)) {
//                onClick(children.get(point.id), mouseX, mouseY, button);
//                break;
//            }
//        }

        searchBox.mouseClicked(mouseX, mouseY, button);
        scrollUpArrow.mouseClicked(mouseX, mouseY, button);
        scrollDownArrow.mouseClicked(mouseX, mouseY, button);
        return true;
    }


    public void onRelease(int mX, int mY) {
        clicked = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        searchBox.render(mouseX, mouseY, particleTicks);
        // TODO add search status
//            if (searchBox.getText().length() > 0 || children.size() > 0) {
//                gui.drawString(Localization.ITEMS_FOUND.toString() + " " + children.size(), AMOUNT_TEXT_X, AMOUNT_TEXT_Y, 0.7F, 0x404040);
//            }

        scrollUpArrow.render(mouseX, mouseY, particleTicks);
        scrollDownArrow.render(mouseX, mouseY, particleTicks);
        // TODO glScissor rule
        for (T child : searchResults) {
            int cy = child.getAbsoluteY();
            int sy = getScrollingStartY();
            if(cy > sy && cy <= sy + getDisplayHeight())
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
        int min = 0;
        int size = getItemSizeWithMargin();
        int max = ((int) (Math.ceil(((float) children.size() / itemsPerRow)) - visibleRows)) * size - (size - ITEM_SIZE);
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

    public void scrollUp() {
        scroll(getScrollSpeed());
    }

    public void scrollDown() {
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
        return searchBox != null;
    }

    public int getItemSize() {
        return ITEM_SIZE;
    }

    public int getItemSizeWithMargin() {
        return ITEM_SIZE_WITH_MARGIN;
    }

    public int getScrollSpeed() {
        return SCROLL_SPEED;
    }
}