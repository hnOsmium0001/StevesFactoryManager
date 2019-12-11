package vswe.stevesfactory.ui.manager.toolbox;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.tool.group.GroupList;

import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public final class ToolboxPanel extends DynamicWidthWidget<IWidget> {

    public static final TextureWrapper GROUP_LIST_ICON = TextureWrapper.ofGUITexture("component_icon/group.png", 16, 16, 0, 0, 16, 16);

    private ClosePanelButton close;
    private List<IWidget> children = new ArrayList<>();

    public ToolboxPanel() {
        super(WidthOccupierType.MIN_WIDTH);
        this.setWidth(8 + RenderingHelper.LEFT_BORDER);

        addChildOnly(new IconToolType<>(GROUP_LIST_ICON, GroupList::new).setName(I18n.format("Groups")));
        addChildOnly(close = new ClosePanelButton());
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    private ToolboxPanel addChildOnly(IWidget widget) {
        children.add(widget);
        widget.setParentWidget(this);
        return this;
    }

    @Override
    public ToolboxPanel addChildren(IWidget widget) {
        addChildOnly(widget);
        reflow();
        return this;
    }

    @Override
    public ToolboxPanel addChildren(Collection<IWidget> widgets) {
        children.addAll(widgets);
        for (IWidget widget : widgets) {
            widget.setParentWidget(this);
        }
        reflow();
        return this;
    }

    @Override
    public void reflow() {
        FlowLayout.vertical(children, RenderingHelper.LEFT_BORDER, 0, 0);
        close.setX(RenderingHelper.getXForAlignedCenter(RenderingHelper.LEFT_BORDER, getWidth(), close.getWidth()));
        close.alignBottom(getHeight());
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        RenderingHelper.renderSideLine(this);
        super.render(mouseX, mouseY, particleTicks);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (isInside(mouseX, mouseY) && button == GLFW_MOUSE_BUTTON_LEFT) {
            getWindow().setFocusedWidget(this);
            return true;
        }
        return false;
    }
}
