package vswe.stevesfactory.library.gui.debug.highlight;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;
import vswe.stevesfactory.library.gui.core.*;

import java.util.List;

import static vswe.stevesfactory.utils.RenderingHelper.drawTransparentRect;

public class BoxHighlighting {

    public static final int CONTENTS = 0x99ff9d82;
    public static final int BORDER = 0x993b86ff;

    public static boolean tryDraw(IWidget widget, int mx, int my) {
        if (widget.isInside(mx, my)) {
            if (widget instanceof IContainer<?>) {
                for (IWidget child : ((IContainer<?>) widget).getChildren()) {
                    if (child.isInside(mx, my)) {
                        return false;
                    }
                }
            }
            draw(widget);
            overlayInfo(mx, my, widget);
            return true;
        }
        return false;
    }

    public static void draw(IWidget widget) {
        int ax = widget.getAbsoluteX();
        int ay = widget.getAbsoluteY();
        drawTransparentRect(ax, ay, ax + widget.getWidth(), ay + widget.getHeight(), CONTENTS);
    }

    public static void overlayInfo(int mx, int my, IWidget widget) {
        List<String> text = ImmutableList.of(
                widget + ":",
                "X=" + widget.getX(),
                "Y=" + widget.getY(),
                "AbsX=" + widget.getAbsoluteX(),
                "AbsY=" + widget.getAbsoluteY(),
                "Width=" + widget.getWidth(),
                "Height=" + widget.getHeight()
        );
        GuiUtils.drawHoveringText(text, mx, my, Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight(), Integer.MAX_VALUE, Minecraft.getInstance().fontRenderer);
    }

    public static boolean tryDraw(IWindow window, int mx, int my) {
        if (window.isInside(mx, my)) {
            for (IWidget child : window.getChildren()) {
                if (child.isInside(mx, my)) {
                    return false;
                }
            }
            draw(window);
            overlayInfo(mx, my, window);
            return true;
        }
        return false;
    }

    public static void draw(IWindow window) {
        int x = window.getX();
        int y = window.getY();
        int x2 = x + window.getWidth();
        int y2 = y + window.getHeight();
        int bs = window.getBorderSize();
        // Can't just do two rectangles because they are transparent
        drawTransparentRect(x, y, x2, y + bs, BORDER);
        drawTransparentRect(x2 - bs, y, x2, y2 - bs, BORDER);
        drawTransparentRect(x + bs, y2 - bs, x2, y2, BORDER);
        drawTransparentRect(x, y + bs, x + bs, y2, BORDER);

        int cx = window.getContentX();
        int cy = window.getContentY();
        drawTransparentRect(cx, cy, cx + window.getContentWidth(), cy + window.getContentHeight(), CONTENTS);
    }

    public static void overlayInfo(int mx, int my, IWindow window) {
        List<String> text = ImmutableList.of(
                window + ":",
                "X=" + window.getX(),
                "Y=" + window.getY(),
                "Width=" + window.getWidth(),
                "Height=" + window.getHeight(),
                "ContentX=" + window.getContentX(),
                "ContentY=" + window.getContentY(),
                "ContentWidth=" + window.getContentWidth(),
                "ContentHeight=" + window.getContentHeight()
        );
        GuiUtils.drawHoveringText(text, mx, my, Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight(), Integer.MAX_VALUE, Minecraft.getInstance().fontRenderer);
    }

}
