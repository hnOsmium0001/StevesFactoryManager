package vswe.stevesfactory.ui.manager.tool;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.contextmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.contextmenu.ContextMenu;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import javax.annotation.Nullable;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

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
            panel.setX(RenderingHelper.LEFT_BORDER + 1);
            panel.setHeight(getHeight());
            getWindow().setFocusedWidget(panel);
        }
        FactoryManagerGUI.getActiveGUI().getTopLevel().reflow();
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    @Override
    public void reflow() {
        IWidget widget = getContainedWidget();
        setWidth(widget == null ? 0 : widget.getWidth() + 2 + RenderingHelper.LEFT_BORDER);
    }

    public IWidget getContainedWidget() {
        return children.isEmpty() ? null : children.get(0);
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
        if (isInside(mouseX, mouseY)) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                getWindow().setFocusedWidget(this);
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                openActionMenu();
            }
            return true;
        }
        return false;
    }

    private void openActionMenu() {
        ContextMenu contextMenu = ContextMenu.atCursor(ImmutableList.of(
                new CallbackEntry(null, "gui.sfm.FactoryManager.Tool.CtxMenu.CloseToolPanel", b -> setActivePanel(null)),
                new CallbackEntry(null, "gui.sfm.FactoryManager.CtxMenu.ToggleFullscreen", b -> FactoryManagerGUI.getActiveGUI().getPrimaryWindow().toggleFullscreen())
        ));
        WidgetScreen.getCurrentScreen().addPopupWindow(contextMenu);
    }
}
