package vswe.stevesfactory.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public final class RenderingHelper {

    private RenderingHelper() {
    }

    public static BufferBuilder getRenderer() {
        return Tessellator.getInstance().getBuffer();
    }

    public static void drawRect(int x, int y, int x2, int y2, int red, int green, int blue, int alpha) {
        BufferBuilder renderer = getRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x, y2, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos(x2, y, 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawRect(int x, int y, int x2, int y2, int color) {
        int alpha = (color >> 24) & 255;
        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        drawRect(x, y, x2, y2, red, green, blue, alpha);
    }

    public static void drawColorLogic(int x, int y, int width, int height, int red, int green, int blue, GlStateManager.LogicOp logicOp) {
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(logicOp);

        drawRect(x, y, width, height, red, green, blue, 255);

        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }

    public static void drawThickBeveledBox(int x1, int y1, int x2, int y2, int thickness, int topLeftColor, int bottomRightColor, int fillColor) {
        if (fillColor != -1) {
            drawRect(x1 + thickness, y1 + thickness, x2 - thickness, y2 - thickness, fillColor);
        }
        drawRect(x1, y1, x2, y2, bottomRightColor);
        drawRect(x1, y1, x2 - thickness, y2 - thickness, topLeftColor);
    }

}
