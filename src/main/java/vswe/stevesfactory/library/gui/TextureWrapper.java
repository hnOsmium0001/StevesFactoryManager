package vswe.stevesfactory.library.gui;

import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;

/**
 * Wraps around a ResourceLocation, used to simplify drawing 2D textures.
 */
public class TextureWrapper {

    private final ResourceLocation texture;
    private final int textureWidth, textureHeight, tx, ty, portionWidth, portionHeight;

    public TextureWrapper(ResourceLocation texture, int textureWidth, int textureHeight, int tx, int ty, int portionWidth, int portionHeight) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.tx = tx;
        this.ty = ty;
        this.portionWidth = portionWidth;
        this.portionHeight = portionHeight;
    }

    public void draw(int x, int y) {
        int x2 = x + portionWidth;
        int y2 = y + portionHeight;
        RenderingHelper.drawTexturePortion(x, y, x2, y2, texture, textureWidth, textureHeight, tx, ty, portionWidth, portionHeight);
    }

    public Dimension getBounds() {
        return new Dimension(portionWidth, portionHeight);
    }

}
