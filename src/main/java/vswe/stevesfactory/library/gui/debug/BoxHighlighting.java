package vswe.stevesfactory.library.gui.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;

import static vswe.stevesfactory.utils.RenderingHelper.*;

public class BoxHighlighting {

    public interface IInspectionInfoProvider {

        void provideInformation(ITextReceiver receiver);
    }

    private static final ITextReceiver DEFAULT_INFO_RENDERER = new ITextReceiver() {
        private static final int STARTING_X = 1;
        private static final int STARTING_Y = 1;
        private int x;
        private int y;

        {
            reset();
        }

        @Override
        public void reset() {
            x = STARTING_X;
            y = STARTING_Y;
        }

        @Override
        public void string(String text) {
            fontRenderer().drawString(text, x, y, Color.WHITE.getRGB());
            x += fontRenderer().getStringWidth(text);
        }

        @Override
        public void line(String line) {
            fontRenderer().drawString(line, STARTING_X, y, Color.WHITE.getRGB());
            nextLine();
        }

        @Override
        public void nextLine() {
            x = STARTING_X;
            y += fontHeight() + 2;
        }
    };

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
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5F, 0.5F, 1.0F);
        DEFAULT_INFO_RENDERER.reset();
        if (widget instanceof IInspectionInfoProvider) {
            ((IInspectionInfoProvider) widget).provideInformation(DEFAULT_INFO_RENDERER);
        } else {
            DEFAULT_INFO_RENDERER.line(widget + ":");
            DEFAULT_INFO_RENDERER.line("X=" + widget.getX());
            DEFAULT_INFO_RENDERER.line("Y=" + widget.getY());
            DEFAULT_INFO_RENDERER.line("AbsX=" + widget.getAbsoluteX());
            DEFAULT_INFO_RENDERER.line("AbsY=" + widget.getAbsoluteY());
            DEFAULT_INFO_RENDERER.line("Width=" + widget.getWidth());
            DEFAULT_INFO_RENDERER.line("Height=" + widget.getHeight());
        }
        GlStateManager.popMatrix();
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
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5F, 0.5F, 1.0F);
        DEFAULT_INFO_RENDERER.reset();
        if (window instanceof IInspectionInfoProvider) {
            ((IInspectionInfoProvider) window).provideInformation(DEFAULT_INFO_RENDERER);
        } else {
            DEFAULT_INFO_RENDERER.line(window + ":");
            DEFAULT_INFO_RENDERER.line("X=" + window.getX());
            DEFAULT_INFO_RENDERER.line("Y=" + window.getY());
            DEFAULT_INFO_RENDERER.line("Width=" + window.getWidth());
            DEFAULT_INFO_RENDERER.line("Height=" + window.getHeight());
            DEFAULT_INFO_RENDERER.line("ContentX=" + window.getContentX());
            DEFAULT_INFO_RENDERER.line("ContentY=" + window.getContentY());
            DEFAULT_INFO_RENDERER.line("ContentWidth=" + window.getContentWidth());
            DEFAULT_INFO_RENDERER.line("ContentHeight=" + window.getContentHeight());
            DEFAULT_INFO_RENDERER.line("BorderSize=" + window.getBorderSize());
        }
        GlStateManager.popMatrix();
    }
}
