package vswe.stevesfactory.library.gui;

import com.google.common.base.MoreObjects;
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
        float uFactor = 1.0F / (float) textureWidth;
        float vFactor = 1.0F / (float) textureHeight;
        int tx2 = tx + portionWidth;
        int ty2 = ty + portionHeight;
        RenderingHelper.drawTexture(x, y, x2, y2, getZLevel(), texture, tx * uFactor, ty * vFactor, tx2 * uFactor, ty2 * vFactor);
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

    public float getZLevel() {
        return 0F;
    }

    public int getPortionWidth() {
        return portionWidth;
    }

    public int getPortionHeight() {
        return portionHeight;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("texture", texture)
                .add("textureWidth", textureWidth)
                .add("textureHeight", textureHeight)
                .add("tx", tx)
                .add("ty", ty)
                .add("portionWidth", portionWidth)
                .add("portionHeight", portionHeight)
                .toString();
    }

    public TextureWrapper withZ(float z) {
        return new TextureWrapperWithZ(this, z);
    }

    private static class TextureWrapperWithZ extends TextureWrapper {

        private float zLevel;

        public TextureWrapperWithZ(TextureWrapper source, float zLevel) {
            super(source.texture, source.textureWidth, source.textureHeight, source.tx, source.ty, source.portionWidth, source.portionHeight);
            this.zLevel = zLevel;
        }

        @Override
        public float getZLevel() {
            return zLevel;
        }
    }

}
