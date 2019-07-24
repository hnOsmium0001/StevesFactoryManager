package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.VectorHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ScrollController<T extends IWidget> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T>, ResizableWidgetMixin {

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

    private int itemsPerRow = 5;
    private int visibleRows = 2;
    private int startX = 5;
    private int scrollingUpperLimit = SEARCH_BOX_Y + SEARCH_BOX_HEIGHT;
    private boolean disabledScroll;

    private int offset;
    private int dir;
    private boolean clicked;
    private boolean selected;
    private List<T> result;

    private TextField searchBox;
    private IWidget scrollUpArrow;
    private IWidget scrollDownArrow;

    public ScrollController(boolean hasSearchBox) {
        this(hasSearchBox ? "" : null);
    }

    public ScrollController(String defaultText) {
        // TODO
        super(0, 0);
        if (defaultText != null) {
            // TODO
//            searchBox = new TextBoxLogic(Integer.MAX_VALUE, SEARCH_BOX_WIDTH - SEARCH_BOX_TEXT_X * 2)
//            {
//                @Override
//                protected void textChanged()
//                {
//                    if (getText().length() > 0)
//                    {
//                        updateSearch();
//                    } else
//                    {
//                        result.clear();
//                        updateScrolling();
//                    }
//                }
//            };
            searchBox = new TextField(0, 0, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT);
            searchBox.setText(defaultText);
        }

        updateSearch();
    }

    protected abstract List<T> updateSearch(String search, boolean all);

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

    private List<Point> getItemCoordinates() {
        List<Point> points = new ArrayList<>();

        int start = getFirstRow();
        for (int row = start; row < start + visibleRows + 1; row++) {
            for (int col = 0; col < itemsPerRow; col++) {
                int id = row * itemsPerRow + col;
                if (id >= 0 && id < result.size()) {
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
        if (VectorHelper.isInside((int) mouseX, (int) mouseY, SEARCH_BOX_X, SEARCH_BOX_Y, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT)) {
            if (button == 0 || !selected) {
                selected = !selected;
            } else if (hasSearchBox()) {
                searchBox.setText("");
            }
        }

        ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button);
//        List<Point> points = getItemCoordinates();
//        for (Point point : points) {
//            if (VectorHelper.isInside((int) mouseX, (int)mouseY, point.x, point.y, ITEM_SIZE, ITEM_SIZE)) {
//                onClick(result.get(point.id), mouseX, mouseY, button);
//                break;
//            }
//        }

        if (inArrowBounds(true, mouseX, mouseY)) {
            clicked = true;
            dir = 1;
        } else if (inArrowBounds(false, mouseX, mouseY)) {
            clicked = true;
            dir = -1;
        }
    }


    public void onRelease(int mX, int mY) {
        clicked = false;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        if (hasSearchBox()) {
            searchBox.render(mouseX, mouseY, particleTicks);
//            int srcBoxY = selected ? 1 : 0;
//            RenderingHelper.drawTexturePortion(SEARCH_BOX_X, SEARCH_BOX_Y, SEARCH_BOX_SRC_X, SEARCH_BOX_SRC_Y + srcBoxY * SEARCH_BOX_HEIGHT, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT);
//            gui.drawString(searchBox.getText(), SEARCH_BOX_X + SEARCH_BOX_TEXT_X, SEARCH_BOX_Y + SEARCH_BOX_TEXT_Y, 0xFFFFFF);
//
//            if (selected) {
//                gui.drawCursor(SEARCH_BOX_X + searchBox.getCursorPosition(gui) + CURSOR_X, SEARCH_BOX_Y + CURSOR_Y, CURSOR_Z, 0xFFFFFFFF);
//            }
//
//            if (searchBox.getText().length() > 0 || result.size() > 0) {
//                gui.drawString(Localization.ITEMS_FOUND.toString() + " " + result.size(), AMOUNT_TEXT_X, AMOUNT_TEXT_Y, 0.7F, 0x404040);
//            }
        }

        if (result.size() > 0) {
            // TODO implement hiding arrows when can't scroll anymore
            scrollUpArrow.render(mouseX, mouseY, particleTicks);
            scrollDownArrow.render(mouseX, mouseY, particleTicks);

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                draw(gui, result.get(point.id), point.x, point.y, CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY));
            }
        }
    }

    private static final int SCROLL_SPEED = 100;

    private float left;

    public void update(float particleTicks) {
        if (clicked && canScroll) {
            particleTicks += left;
            int change = (int) (particleTicks * SCROLL_SPEED);
            left = particleTicks - (change / (float) SCROLL_SPEED);


            moveOffset(change * dir);
        }
    }

    private void moveOffset(int change) {
        offset += change;
        int min = 0;
        int max = ((int) (Math.ceil(((float) result.size() / itemsPerRow)) - visibleRows)) * ITEM_SIZE_WITH_MARGIN - (ITEM_SIZE_WITH_MARGIN - ITEM_SIZE);
        if (offset < min) {
            offset = min;
        } else if (offset > max) {
            offset = max;
        }
    }

    private void drawArrow(boolean down, int mX, int mY) {
        // TODO
//        if (canScroll) {
//            int srcArrowX = canScroll ? clicked && down == (dir == 1) ? 2 : inArrowBounds(down, mX, mY) ? 1 : 0 : 3;
//            int srcArrowY = down ? 1 : 0;
//
//            RenderingHelper.drawTexturePortion(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SRC_X + srcArrowX * ARROW_WIDTH, ARROW_SRC_Y + srcArrowY * ARROW_HEIGHT, TextureWrapper.FLOW_COMPONENTS, ARROW_WIDTH, ARROW_HEIGHT);
//        }
    }

    private boolean inArrowBounds(boolean down, int mX, int mY) {
        return CollisionHelper.inBounds(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_WIDTH, ARROW_HEIGHT, mX, mY);
    }

    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        List<Point> points = getItemCoordinates();
        for (Point point : points) {
            if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                drawMouseOver(gui, result.get(point.id), mX, mY);
            }
        }
    }


    public void updateScrolling() {
        boolean canScroll = result.size() > itemsPerRow * visibleRows;
        if (!canScroll) {
            offset = 0;
            scrollUpArrow =
        }
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

    public void updateSearch() {
        if (hasSearchBox()) {
            result = updateSearch(searchBox.getText().toLowerCase(), searchBox.getText().toLowerCase().equals(".all"));
        } else {
            result = updateSearch("", false);
        }
        updateScrolling();
    }

    public List<T> getResult() {
        return result;
    }

    public void setText(String s) {
        searchBox.setText(s);
        updateSearch();
    }

    public String getText() {
        return searchBox.getText();
    }

    public void doScroll(int scroll) {
        if (!disabledScroll) {
            moveOffset(scroll / -20);
        }
    }

    public void setDisabledScroll(boolean disabledScroll) {
        this.disabledScroll = disabledScroll;
    }

    public boolean hasSearchBox() {
        return searchBox != null;
    }
}