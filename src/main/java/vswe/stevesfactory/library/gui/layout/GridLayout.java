package vswe.stevesfactory.library.gui.layout;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.widget.INamedElement;
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
        setGridGap(gridGap);
        return this;
    }

    @CanIgnoreReturnValue
    public GridLayout rows(int rows) {
        setRows(rows);
        return this;
    }

    @CanIgnoreReturnValue
    public GridLayout columns(int columns) {
        setColumns(columns);
        return this;
    }

    /**
     * The width of each column will be {@code n/s} pixels of the width of the bond widget minus the gaps, where {@code n} is the array
     * element, {@code s} is the <i>sum</i> of all elements in the array.
     */
    @CanIgnoreReturnValue
    public GridLayout widths(int... widthFactors) {
        setColumnWidths(widthFactors);
        return this;
    }

    /**
     * The width of each row will be {@code n/s} pixels of the height of the bond widget minus the gaps, where {@code n} is the array
     * element, {@code s} is the <i>sum</i> of all elements in the array.
     */
    @CanIgnoreReturnValue
    public GridLayout heights(int... heightFactors) {
        setRowHeights(heightFactors);
        return this;
    }

    @CanIgnoreReturnValue
    public GridLayout areas(int... areas) {
        this.setAreas(areas);
        return this;
    }

    /**
     * A special version of the regular reflow mechanism, where areas can be named. The names should come from the widgets; if some names
     * are not defined, it will use the first widget in the list by default.
     */
    public <T extends IWidget & INamedElement & RelocatableWidgetMixin & ResizableWidgetMixin> void reflow(List<T> widgets, String[] template) {
        int[][] areas = new int[rows][columns];
        Object2IntMap<String> m = new Object2IntOpenHashMap<>();
        for (int i = 0; i < widgets.size(); i++) {
            T widget = widgets.get(i);
            m.put(widget.getName(), i);
        }
        // Check for no repeating names
        Preconditions.checkArgument(m.size() == widgets.size());

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                areas[y][x] = m.getInt(template[y * columns + x]);
            }
        }
        reflow(widgets, areas);
    }

    public <T extends IWidget & RelocatableWidgetMixin & ResizableWidgetMixin> void reflow(List<T> widgets) {
        reflow(widgets, this.areas);
    }

    // px/py stands for "Pixel-position x/y"
    // gx/gy stands for "Grid x/y"
    public <T extends IWidget & RelocatableWidgetMixin & ResizableWidgetMixin> void reflow(List<T> widgets, int[][] areas) {
        for (int gy = 0; gy < areas.length; gy++) {
            int[] row = areas[gy];
            for (int gx = 0; gx < row.length; gx++) {
                int cell = row[gx];
                // Discard nonexistent indexes
                if (cell > widgets.size()) {
                    continue;
                }

                T widget = widgets.get(cell);
                if (!BoxSizing.shouldIncludeWidget(widget)) {
                    continue;
                }

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

    public void setGridGap(int gridGap) {
        Preconditions.checkArgument(gridGap >= 0);
        this.gridGap = gridGap;
    }

    public void setRows(int rows) {
        Preconditions.checkArgument(rows > 0);
        this.rows = rows;
        if (rowHeights != null && rows != rowHeights.length) {
            rowHeights = null;
        }
    }

    public void setColumns(int columns) {
        Preconditions.checkArgument(columns > 0);
        this.columns = columns;
        if (columnWidths != null && columns != columnWidths.length) {
            columnWidths = null;
        }
    }

    public void setColumnWidths(int[] widthFactors) {
        int sum = Arrays.stream(widthFactors).sum();
        int usableWidth = getDimensions().width - getHorizontalSumGaps();
        for (int i = 0; i < widthFactors.length; i++) {
            float factor = (float) widthFactors[i] / sum;
            columnWidths[i] = (int) (usableWidth * factor);
        }
    }

    public void setRowHeights(int[] heightFactors) {
        int sum = Arrays.stream(heightFactors).sum();
        int usableHeight = getDimensions().height - getVerticalSumGaps();
        for (int i = 0; i < heightFactors.length; i++) {
            float factor = (float) heightFactors[i] / sum;
            rowHeights[i] = (int) (usableHeight * factor);
        }
    }

    public void setAreas(int[] areas) {
        Preconditions.checkArgument(areas.length == rows * columns);
        this.areas = new int[rows][columns];
        for (int y = 0; y < rows; y++) {
            System.arraycopy(areas, y * columns, this.areas[y], 0, rows);
        }
    }
}
