package vswe.stevesfactory.library.gui.widget.button;

import com.google.common.base.Preconditions;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;

import java.awt.*;
import java.util.function.IntConsumer;

/**
 * A ready-to-use icon button implementation that stores each mouse state texture.
 */
public class SimpleIconButton extends AbstractIconButton implements ResizableWidgetMixin {

    private TextureWrapper textureNormal;
    private TextureWrapper textureHovering;
    private IntConsumer onClick;

    public SimpleIconButton(int x, int y, TextureWrapper textureNormal, TextureWrapper textureHovering) {
        super(x, y, 0, 0);
        this.setTextures(textureNormal, textureHovering);
    }

    public SimpleIconButton(Point location, Dimension dimensions, TextureWrapper textureNormal, TextureWrapper textureHovering) {
        super(location, new Dimension());
        this.setTextures(textureNormal, textureHovering);
    }

    @Override
    public TextureWrapper getTextureNormal() {
        return textureNormal;
    }

    @Override
    public TextureWrapper getTextureHovered() {
        return textureHovering;
    }

    public void setTextureNormal(TextureWrapper textureNormal) {
        checkArguments(textureNormal, textureHovering);
        this.textureNormal = textureNormal;
        this.setDimensions(textureNormal.getPortionWidth(), textureNormal.getPortionHeight());
    }

    public void setTextureHovering(TextureWrapper textureHovering) {
        checkArguments(textureNormal, textureHovering);
        this.textureHovering = textureHovering;
        this.setDimensions(textureHovering.getPortionWidth(), textureHovering.getPortionHeight());
    }

    public void setTextures(TextureWrapper textureNormal, TextureWrapper textureHovering) {
        checkArguments(textureNormal, textureHovering);
        this.textureNormal = textureNormal;
        this.textureHovering = textureHovering;
        // Either one is fine, since we checked that they are the same size
        this.setDimensions(textureNormal.getPortionWidth(), textureNormal.getPortionHeight());
    }

    private static void checkArguments(TextureWrapper textureNormal, TextureWrapper textureHovering) {
        Preconditions.checkArgument(textureNormal.getBounds().equals(textureHovering.getBounds()));
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        super.render(mouseX, mouseY, particleTicks);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        onClick.accept(button);
        return true;
    }

    public void onClick(IntConsumer onClick) {
        this.onClick = onClick;
    }
}
