package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import java.awt.*;

public abstract class AbstractIconButton extends AbstractWidget implements RelocatableWidgetMixin, LeafWidgetMixin {

    public AbstractIconButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public AbstractIconButton(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        if (isInside(mouseX, mouseY) && isEnabled()) {
            getTextureHovering().draw(getAbsoluteX(), getAbsoluteY());
        } else {
            getTextureNormal().draw(getAbsoluteX(), getAbsoluteY());
        }
    }

    public abstract TextureWrapper getTextureNormal();

    public abstract TextureWrapper getTextureHovering();

}
