package vswe.stevesfactory.library.gui.layout;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Layout widgets on a non-fixed dimension grid, where each row and column have their individual size. The widgets are layed on the grid so
 * that they cover a rectangle of cells.
 * <p>
 * See CSS Grid Layout. This class is meant to replicate the mechanics of it.
 */
public class GridLayout {

    private IContainer<?> bondWidget;
    private int gridGap = 0;

    private int rows;
    private int columns;
    private int[] rowHeights;
    private int[] columnWidths;

    /**
     * A map of where the child widgets should occupy. See CSS Grid's {@code grid-template-areas}. Each element is the ID (index) of the
     * child widget. Note that this array should be setup in [y][x], not [x][y].
     */
    private int[][] areas;

    public GridLayout(IContainer<?> bondWidget) {
        this.bondWidget = bondWidget;
    }

    @CanIgnoreReturnValue
    public GridLayout gridGap(int gridGap) {
        Preconditions.checkArgument(gridGap >= 0);
        this.gridGap = gridGap;
        return this;
    }

    @CanIgnoreReturnValue
    public GridLayout rows(int rows) {
        Preconditions.checkArgument(rows > 0);
        this.rows = rows;
        if (rowHeights != null && rows != rowHeights.length) {
            rowHeights = null;
        }
        return this;
    }

    @CanIgnoreReturnValue
    public GridLayout columns(int columns) {
        Preconditions.checkArgument(columns > 0);
        this.columns = columns;
        if (columnWidths != null && columns != columnWidths.length) {
            columnWidths = null;
        }
        return this;
    }

    /**
     * The width of each column will be {@code n/s} pixels of the width of the bond widget minus the gaps, where {@code n} is the array
     * element, {@code s} is the <i>sum</i> of all elements in the array.
     */
    @CanIgnoreReturnValue
    public GridLayout widths(int... widthFactors) {
        int sum = Arrays.stream(widthFactors).sum();
        int usableWidth = getDimensions().width - getHorizontalSumGaps();
        for (int i = 0; i < widthFactors.length; i++) {
            float factor = (float) widthFactors[i] / sum;
            columnWidths[i] = (int) (usableWidth * factor);
        }
        return this;
    }

    /**
     * The width of each row will be {@code n/s} pixels of the height of the bond widget minus the gaps, where {@code n} is the array
     * element, {@code s} is the <i>sum</i> of all elements in the array.
     */
    @CanIgnoreReturnValue
    public GridLayout heights(int... heightFactors) {
        int sum = Arrays.stream(heightFactors).sum();
        int usableHeight = getDimensions().height - getVerticalSumGaps();
        for (int i = 0; i < heightFactors.length; i++) {
            float factor = (float) heightFactors[i] / sum;
            rowHeights[i] = (int) (usableHeight * factor);
        }
        return this;
    }

    @CanIgnoreReturnValue
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
            result += columnWidths[i] + gridGap;
        }
        return result;
    }

    private int getPyAt(int gy) {
        int result = 0;
        for (int i = 0; i < gy; i++) {
            result += rowHeights[i] + gridGap;
        }
        return result;
    }

    private int getPx2At(int gx) {
        return getPxAt(gx) + columnWidths[gx];
    }

    private int getPy2At(int gy) {
        return getPyAt(gy) + rowHeights[gy];
    }

    public IContainer<?> getBondWidget() {
        return bondWidget;
    }

    public Dimension getDimensions() {
        return bondWidget.getDimensions();
    }

    public int getGridGap() {
        return gridGap;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    private int getHorizontalSumGaps() {
        return (columns - 1) * gridGap;
    }

    private int getVerticalSumGaps() {
        return (rows - 1) * gridGap;
    }
}
