package vswe.stevesfactory.library.gui.layout;

import com.google.common.base.Preconditions;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;

import java.util.List;

public class GridLayout {

    private int gridGap;

    private int rows;
    private int columns;
    private int[] rowWidths;
    private int[] colHeights;

    /**
     * A map of where the child widgets should occupy. See CSS Grid's {@code grid-template-areas}. Each element is the ID (index) of the
     * child widget. Note that this array should be setup in [y][x], not [x][y].
     */
    private int[][] areas;

    public GridLayout gap(int gridGap) {
        Preconditions.checkArgument(gridGap >= 0);
        this.gridGap = gridGap;
        return this;
    }

    public GridLayout rows(int rows) {
        Preconditions.checkArgument(rows > 0);
        this.rows = rows;
        return this;
    }

    // TODO support fractions
    public GridLayout widths(int... rowWidths) {
        return this;
    }

    public GridLayout heights(int... colHeights) {
        return this;
    }

    public GridLayout columns(int columns) {
        Preconditions.checkArgument(columns > 0);
        this.columns = columns;
        return this;
    }

    public GridLayout areas(int... areas) {
        Preconditions.checkArgument(areas.length == rows * columns);
        for (int y = 0; y < columns; y++) {
            System.arraycopy(areas, y * columns, this.areas[y], 0, rows);
        }
        return this;
    }

    // TODO support named areas

    // px/py stands for "Pixel-position x/y"
    // gx/gy stands for "Grid x/y"
    public <T extends IWidget & RelocatableWidgetMixin & ResizableWidgetMixin> void applyBoxes(List<T> widgets) {
        int maxIndex = 0;
        for (int gy = 0; gy < areas.length; gy++) {
            int[] row = areas[gy];
            for (int gx = 0; gx < row.length; gx++) {
                int cell = row[gx];

                maxIndex = Math.max(maxIndex, cell);
                if (maxIndex > widgets.size()) {
                    throw new IllegalArgumentException();
                }

                T widget = widgets.get(cell);
                int px = getPxAt(gx);
                int py = getPyAt(gy);
                // Expand the first vertex towards top left
                if (!widget.isInside(px, py)) {
                    widget.setLocation(Math.min(widget.getX(), py), Math.min(widget.getY(), py));
                }

                int px2 = getPx2At(gx);
                int py2 = getPy2At(gy);
                // Expand the second vertex towards bottom right
                if (!widget.isInside(px2, py2)) {
                    widget.setDimensions(Math.max(widget.getWidth(), px2 - px), Math.max(widget.getHeight(), py2 - py));
                }
            }
        }
    }

    private int getPxAt(int gx) {
        int result = 0;
        for (int i = 0; i < gx; i++) {
            result += rowWidths[i] + gridGap;
        }
        return result;
    }

    private int getPyAt(int gy) {
        int result = 0;
        for (int i = 0; i < gy; i++) {
            result += colHeights[i] + gridGap;
        }
        return result;
    }

    private int getPx2At(int gx) {
        return getPxAt(gx) + rowWidths[gx];
    }

    private int getPy2At(int gy) {
        return getPyAt(gy) + colHeights[gy];
    }
}
