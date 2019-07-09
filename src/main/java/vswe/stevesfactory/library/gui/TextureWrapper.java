package vswe.stevesfactory.library.gui;

import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;

/**
 * Wraps around a ResourceLocation, used to simplify drawing 2D textures.
 */
public class TextureWrapper {

    public static final ResourceLocation FLOW_COMPONENTS = new ResourceLocation(StevesFactoryManager.MODID, "textures/gui/flow_components.png");

    public static TextureWrapper ofFlowComponent(int tx, int ty, int portionWidth, int portionHeight) {
        return new TextureWrapper(FLOW_COMPONENTS, 256, 256, tx, ty, portionWidth, portionHeight);
    }

    public static TextureWrapper ofGUITexture(String path, int textureWidth, int textureHeight, int tx, int ty, int portionWidth, int portionHeight) {
        return new TextureWrapper("textures/gui/" + path, textureWidth, textureHeight, tx, ty, portionWidth, portionHeight);
    }

    private final ResourceLocation texture;
    private final int textureWidth, textureHeight, tx, ty, portionWidth, portionHeight;

    public TextureWrapper(String path, int textureWidth, int textureHeight, int tx, int ty, int portionWidth, int portionHeight) {
        this(new ResourceLocation(StevesFactoryManager.MODID, path), textureWidth, textureHeight, tx, ty, portionWidth, portionHeight);
    }

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

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public int getTx() {
        return tx;
    }

    public int getTy() {
        return ty;
    }

    public int getPortionWidth() {
        return portionWidth;
    }

    public int getPortionHeight() {
        return portionHeight;
    }
}
