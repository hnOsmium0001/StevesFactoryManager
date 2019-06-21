package vswe.stevesfactory.library.gui.layout;

import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

public class AligningFlowLayout<T extends IWidget & RelocatableWidgetMixin> extends UnruledFlowLayout<T> {

    public enum Alignment {
        LEFT {
            @Override
            public void alignTo(RelocatableWidgetMixin widget, int x) {
                widget.setX(x);
            }
        },
        RIGHT {
            @Override
            public void alignTo(RelocatableWidgetMixin widget, int x) {
                int leftX = x - widget.getWidth();
                if (leftX >= 0) {
                    widget.setX(leftX);
                } else {
                    widget.setX(0);
                }
            }
        };

        public abstract void alignTo(RelocatableWidgetMixin widget, int x);
    }

    public Alignment alignment;
    public int alignmentX;

    public AligningFlowLayout(Alignment alignment, int alignmentX) {
        this.alignment = alignment;
        this.alignmentX = alignmentX;
    }

    @Override
    public void adjustPosition(T widget, int y) {
        super.adjustPosition(widget, y);
        alignment.alignTo(widget, alignmentX);
    }

    @Override
    public LayoutType getType() {
        return LayoutType.StatedLayout;
    }

}
