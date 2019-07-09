package vswe.stevesfactory.library.gui.debug.highlight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;

import static vswe.stevesfactory.utils.RenderingHelper.drawTransparentRect;
import static vswe.stevesfactory.utils.RenderingHelper.vertexTransparentRect;

public class BoxHighlighting {

    public static boolean enabled = true;

    public static final int CONTENTS = 0x99ff9d82;
    public static final int BORDER = 0x993b86ff;
    public static final float BORDER_A = BORDER >> 24 & 255;
    public static final float BORDER_R = BORDER >> 16 & 255;
    public static final float BORDER_G = BORDER >> 8 & 255;
    public static final float BORDER_B = BORDER & 255;

    public static boolean tryDraw(IWidget widget, int mx, int my) {
        if (!enabled) {
            return false;
        }
        if (widget.isInside(mx, my)) {
            if (widget instanceof IContainer<?>) {
                for (IWidget child : ((IContainer<?>) widget).getChildren()) {
                    if (child.isInside(mx, my)) {
                        return false;
                    }
                }
            }
            draw(widget);
            return true;
        }
        return false;
    }

    public static void draw(IWidget widget) {
        int ax = widget.getAbsoluteX();
        int ay = widget.getAbsoluteY();
        drawTransparentRect(ax, ay, ax + widget.getWidth(), ay + widget.getHeight(), CONTENTS);

        if (Screen.hasControlDown()) {
            overlayInfo(widget);
        }
    }

    public static void overlayInfo(IWidget widget) {
        overlayInfo(new String[]{
                widget + ":",
                "X=" + widget.getX(),
                "Y=" + widget.getY(),
                "AbsX=" + widget.getAbsoluteX(),
                "AbsY=" + widget.getAbsoluteY(),
                "Width=" + widget.getWidth(),
                "Height=" + widget.getHeight()
        });
    }

    public static boolean tryDraw(IWindow window, int mx, int my) {
        if (!enabled) {
            return false;
        }
        if (window.isInside(mx, my)) {
            for (IWidget child : window.getChildren()) {
                if (child.isInside(mx, my)) {
                    return false;
                }
            }
            draw(window);
            return true;
        }
        return false;
    }

    public static void draw(IWindow window) {
        // Can't just do two rectangles because they are transparent
        RenderingHelper.preDrawTransparentRect();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        {
            int x = window.getX();
            int y = window.getY();
            int x2 = x + window.getWidth();
            int y2 = y + window.getHeight();
            int bs = window.getBorderSize();

            vertexTransparentRect(buffer, x, y, x2 - bs, y + bs, BORDER_A, BORDER_R, BORDER_G, BORDER_B);
            vertexTransparentRect(buffer, x2 - bs, y, x2, y2 - bs, BORDER_A, BORDER_R, BORDER_G, BORDER_B);
            vertexTransparentRect(buffer, x + bs, y2 - bs, x2, y2, BORDER_A, BORDER_R, BORDER_G, BORDER_B);
            vertexTransparentRect(buffer, x, y + bs, x + bs, y2, BORDER_A, BORDER_R, BORDER_G, BORDER_B);
        }
        {
            int cx = window.getContentX();
            int cy = window.getContentY();
            vertexTransparentRect(buffer, cx, cy, cx + window.getContentWidth(), cy + window.getContentHeight(), CONTENTS);
        }
        tessellator.draw();
        RenderingHelper.postDrawTransparentRect();

        if (Screen.hasControlDown()) {
            overlayInfo(window);
        }
    }

    public static void overlayInfo(IWindow window) {
        overlayInfo(new String[]{
                window + ":",
                "X=" + window.getX(),
                "Y=" + window.getY(),
                "Width=" + window.getWidth(),
                "Height=" + window.getHeight(),
                "ContentX=" + window.getContentX(),
                "ContentY=" + window.getContentY(),
                "ContentWidth=" + window.getContentWidth(),
                "ContentHeight=" + window.getContentHeight(),
                "BorderSize=" + window.getBorderSize()
        });
    }

    private static void overlayInfo(String[] texts) {
        int x = 1;
        int y = 1;
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        for (String s : texts) {
            fontRenderer.drawString(s, x, y, Color.WHITE.getRGB());
            y += fontRenderer.FONT_HEIGHT + 2;
        }
    }
}
