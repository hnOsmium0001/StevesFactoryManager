package vswe.stevesfactory.ui.manager.toolbox;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.tool.group.GroupList;

import java.util.*;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static vswe.stevesfactory.library.gui.RenderingHelper.rectVertices;

public final class ToolboxPanel extends DynamicWidthWidget<IToolType> {

    public static final TextureWrapper GROUP_LIST_ICON = TextureWrapper.ofGUITexture("component_icon/group.png", 16, 16, 0, 0, 16, 16);
    public static final int LEFT_BORDER = 2;

    private List<IToolType> children = new ArrayList<>();

    public ToolboxPanel() {
        super(WidthOccupierType.MIN_WIDTH);
        this.setWidth(8 + LEFT_BORDER);

        addChildren(new IconToolType(GROUP_LIST_ICON, GroupList::new).setName(I18n.format("Groups")));
    }

    @Override
    public Collection<IToolType> getChildren() {
        return children;
    }

    @Override
    public ToolboxPanel addChildren(IToolType widget) {
        children.add(widget);
        widget.setParentWidget(this);
        reflow();
        return this;
    }

    @Override
    public ToolboxPanel addChildren(Collection<IToolType> widgets) {
        children.addAll(widgets);
        for (IToolType widget : widgets) {
            widget.setParentWidget(this);
        }
        reflow();
        return this;
    }

    @Override
    public void reflow() {
        FlowLayout.vertical(children, LEFT_BORDER, 0, 0);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        renderSideLine();
        super.render(mouseX, mouseY, particleTicks);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    private void renderSideLine() {
        GlStateManager.disableTexture();
        Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int x1 = getAbsoluteX();
        int x2 = x1 + LEFT_BORDER;
        int y1 = getAbsoluteY() - 1;
        int y2 = getAbsoluteYBottom() + 1;
        rectVertices(x1, y1, x2, y2, 0xff797979);
        rectVertices(x1 + 1, y1, x2, y2, 0xffffffff);
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture();
    }
}
