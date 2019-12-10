package vswe.stevesfactory.ui.manager.tool;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.blocks.FactoryManagerTileEntity;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static vswe.stevesfactory.library.gui.RenderingHelper.rectVertices;
import static vswe.stevesfactory.library.gui.RenderingHelper.usePlainColorGLStates;

public final class ToolPanel extends DynamicWidthWidget<IWidget> {

    private List<IWidget> children = ImmutableList.of();

    public ToolPanel() {
        super(WidthOccupierType.MIN_WIDTH);
    }

    public <T extends IWidget & ResizableWidgetMixin> void setActivePanel(@Nullable T panel) {
        if (panel == null) {
            children = ImmutableList.of();
        } else {
            children = ImmutableList.of(panel);
            panel.setParentWidget(this);
            panel.setHeight(getHeight());
        }
        FactoryManagerGUI.getActiveGUI().getPrimaryWindow().topLevel.reflow();
    }

    @Override
    public Collection<IWidget> getChildren() {
        return children;
    }

    @Override
    public void reflow() {
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        renderBackground();
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    private void renderBackground() {
        GlStateManager.disableAlphaTest();
        usePlainColorGLStates();
        Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int x1 = getX();
        int y1 = getY();
        int x2 = x1 + getWidth();
        int y2 = y1 + getHeight();
        rectVertices(x1, y1, x2, y2, getNormalBorderColor());
        rectVertices(x1 + 1, y1 + 1, x2 - 1, y2 - 1, getNormalBackgroundColor());
        Tessellator.getInstance().draw();
        GlStateManager.enableAlphaTest();
    }

    public int getNormalBorderColor() {
        return 0xff8c8c8c;
    }

    public int getNormalBackgroundColor() {
        return 0xff737373;
    }
}
