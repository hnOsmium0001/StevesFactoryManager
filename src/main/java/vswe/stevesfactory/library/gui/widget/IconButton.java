package vswe.stevesfactory.library.gui.widget;

import com.google.common.base.Preconditions;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import java.awt.*;

public final class IconButton extends AbstractIconButton {

    private TextureWrapper textureNormal;
    private TextureWrapper textureHovering;

    public IconButton(int x, int y, int width, int height, TextureWrapper textureNormal, TextureWrapper textureHovering) {
        super(x, y, width, height);
        this.setTextures(textureNormal, textureHovering);
    }

    public IconButton(Point location, Dimension dimensions, TextureWrapper textureNormal, TextureWrapper textureHovering) {
        super(location, dimensions);
        this.setTextures(textureNormal, textureHovering);
    }

    @Override
    public TextureWrapper getTextureNormal() {
        return textureNormal;
    }

    @Override
    public TextureWrapper getTextureHovering() {
        return textureHovering;
    }

    public void setTextureNormal(TextureWrapper textureNormal) {
        checkArguments(textureNormal, textureHovering);
        this.textureNormal = textureNormal;
    }

    public void setTextureHovering(TextureWrapper textureHovering) {
        checkArguments(textureNormal, textureHovering);
        this.textureHovering = textureHovering;
    }

    public void setTextures(TextureWrapper textureNormal, TextureWrapper textureHovering) {
        checkArguments(textureNormal, textureHovering);
        this.textureNormal = textureNormal;
        this.textureHovering = textureHovering;
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
}
