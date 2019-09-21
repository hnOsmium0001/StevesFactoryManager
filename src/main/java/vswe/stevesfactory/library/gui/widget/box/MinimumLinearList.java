package vswe.stevesfactory.library.gui.widget.box;

import vswe.stevesfactory.library.gui.widget.IWidget;

public class MinimumLinearList<T extends IWidget> extends LinearList<T> {

    public MinimumLinearList(int width, int height) {
        super(width, height);
    }

    @Override
    protected boolean isDrawingScrollBar() {
        return false;
    }

    @Override
    public int getBorder() {
        return 0;
    }

    @Override
    public int getMarginMiddle() {
        return 0;
    }
}
