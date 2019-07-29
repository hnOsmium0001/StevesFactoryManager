package vswe.stevesfactory.library.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.IContainer;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.RenderingHelper;
import vswe.stevesfactory.utils.Utils;

import java.util.*;

/**
 * Code adapted from {@link net.minecraftforge.client.gui.ScrollPanel}
 */
public class ScrollList<T extends IWidget & RelocatableWidgetMixin> extends AbstractWidget implements IContainer<T>, ContainerWidgetMixin<T>, RelocatableContainerMixin<T> {

    private boolean scrolling;
    protected float scrollDistance;

    private List<T> children = new ArrayList<>();

    private final int barLeftX;

    public ScrollList(int width, int height) {
        super(width, height);
        this.barLeftX = getX() + width - getBarWidth();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrolling = button == 0 && mouseX >= barLeftX && mouseX < barLeftX + getBarWidth();
        if (scrolling) {
            return true;
        }
        int mouseListY = ((int) mouseY) - getAbsoluteY() - getContentHeight() + (int) scrollDistance - getBorder();
        if (mouseX >= getAbsoluteX() && mouseX <= getAbsoluteXBR() && mouseListY < 0) {
            return onPanelClicked(mouseX - getAbsoluteX(), mouseY - getAbsoluteY() + (int) scrollDistance - getBorder(), button);
        }

        ContainerWidgetMixin.super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean ret = scrolling;
        scrolling = false;
        ContainerWidgetMixin.super.mouseReleased(mouseX, mouseY, button);
        return ret;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrolling) {
            int maxScroll = getHeight() - getBarHeight();
            double moved = deltaY / maxScroll;
            scrollDistance += getMaxScroll() * moved;
            applyScrollLimits();
            reflow();
            return true;
        }
        ContainerWidgetMixin.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll != 0) {
            scrollDistance += -scroll * getScrollAmount();
            applyScrollLimits();
            reflow();
            return true;
        }
        ContainerWidgetMixin.super.mouseScrolled(mouseX, mouseY, scroll);
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        drawBackground();

        int left = getAbsoluteX();
        int top = getAbsoluteY();
        int right = getAbsoluteXBR();
        int bottom = getAbsoluteYBR();
        int width = getWidth();
        int height = getHeight();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder renderer = tess.getBuffer();

        double scale = minecraft().mainWindow.getGuiScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (left * scale), (int) (minecraft().mainWindow.getHeight() - (bottom * scale)),
                (int) (width * scale), (int) (height * scale));

        for (T child : getChildren()) {
            child.render(mouseX, mouseY, partialTicks);
        }
        drawOverlay();


        int extraHeight = (getContentHeight() + getBorder()) - height;
        if (extraHeight > 0) {
            int barHeight = getBarHeight();
            int barTop = Utils.lowerBound((int) scrollDistance * (height - barHeight) / extraHeight + top, top);

            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            renderer.pos(barLeftX, bottom, 0.0D).tex(0.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            renderer.pos(barLeftX + getBarWidth(), bottom, 0.0D).tex(1.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            renderer.pos(barLeftX + getBarWidth(), top, 0.0D).tex(1.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            renderer.pos(barLeftX, top, 0.0D).tex(0.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();

            renderer.pos(barLeftX, barTop + barHeight, 0.0D).tex(0.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            renderer.pos(barLeftX + getBarWidth(), barTop + barHeight, 0.0D).tex(1.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            renderer.pos(barLeftX + getBarWidth(), barTop, 0.0D).tex(1.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            renderer.pos(barLeftX, barTop, 0.0D).tex(0.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();

            renderer.pos(barLeftX, barTop + barHeight - 1, 0.0D).tex(0.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            renderer.pos(barLeftX + getBarWidth() - 1, barTop + barHeight - 1, 0.0D).tex(1.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            renderer.pos(barLeftX + getBarWidth() - 1, barTop, 0.0D).tex(1.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            renderer.pos(barLeftX, barTop, 0.0D).tex(0.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.draw();
            GlStateManager.enableTexture();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    protected void drawOverlay() {
        int left = getAbsoluteX();
        int top = getAbsoluteY();
        int right = getAbsoluteXBR();
        int bottom = getAbsoluteYBR();

        if (minecraft().world != null) {
            GuiUtils.drawGradientRect(0, left, top, right, bottom, 0xC0101010, 0xD0101010);
        } else {
            // Draw dark dirt background
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            minecraft().getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            float texScale = 32.0F;
            BufferBuilder renderer = RenderingHelper.getRenderer();
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            renderer.pos(left, bottom, 0.0D).tex(left / texScale, (bottom + (int) scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            renderer.pos(right, bottom, 0.0D).tex(right / texScale, (bottom + (int) scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            renderer.pos(right, top, 0.0D).tex(right / texScale, (top + (int) scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            renderer.pos(left, top, 0.0D).tex(left / texScale, (top + (int) scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            Tessellator.getInstance().draw();
        }
    }

    protected void drawBackground() {
    }

    @Override
    public List<T> getChildren() {
        return children;
    }

    @Override
    public void reflow() {
        int offset = (int) -scrollDistance;
        int y = getBorder();
        for (T child : children) {
            child.setY(y + offset);
            y += child.getHeight() + getMarginMiddle();
        }
    }

    @Override
    public ScrollList<T> addChildren(T widget) {
        children.add(widget);
        return this;
    }

    @Override
    public ScrollList<T> addChildren(Collection<T> widgets) {
        children.addAll(widgets);
        return this;
    }

    protected boolean onPanelClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    protected int getContentHeight() {
        int contentHeight = 0;
        for (T child : children) {
            contentHeight += child.getHeight() + getMarginMiddle();
        }
        // Remove last unnecessary border
        return contentHeight - getMarginMiddle();
    }

    public int getBarHeight() {
        int height = getHeight();
        return MathHelper.clamp((height * height) / getContentHeight(), 32, height - getBorder() * 2);
    }

    public int getFirstRowY() {
        return getAbsoluteY() + getBorder();
    }

    public int getScrollAmount() {
        return 20;
    }

    public int getMarginMiddle() {
        return 10;
    }

    public int getMaxScroll() {
        return getContentHeight() - (getHeight() - getBorder());
    }

    private void applyScrollLimits() {
        int max = getMaxScroll();
        if (max < 0) {
            max /= 2;
        }
        scrollDistance = MathHelper.clamp(scrollDistance, 0.0F, max);
    }

    public int getBarWidth() {
        return 6;
    }

    public int getBorder() {
        return 4;
    }
}
