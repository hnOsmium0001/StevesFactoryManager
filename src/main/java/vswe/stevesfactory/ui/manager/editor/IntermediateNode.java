package vswe.stevesfactory.ui.manager.editor;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static vswe.stevesfactory.library.gui.RenderingHelper.rectVertices;

public class IntermediateNode extends AbstractWidget implements INode, LeafWidgetMixin {

    public static final int BORDER = 0xff4d4d4d;
    public static final int NORMAL_FILLER = 0xff9d9d9d;
    public static final int HOVERED_FILLER = 0xffc7c7c7;

    public static final int NODE_WIDTH = 6;
    public static final int NODE_HEIGHT = 6;

    private INode previous;
    private INode next;

    private int initialDragLocalX = -1;
    private int initialDragLocalY = -1;

    public IntermediateNode() {
        super(0, 0, NODE_WIDTH, NODE_HEIGHT);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

        GlStateManager.disableTexture();
        Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int x1 = getAbsoluteX();
        int y1 = getAbsoluteY();
        int x2 = getAbsoluteXRight();
        int y2 = getAbsoluteYBottom();
        rectVertices(x1, y1, x2, y2, BORDER);
        rectVertices(x1 + 1, y1 + 1, x2 - 1, y2 - 1, Utils.isInside(mouseX, mouseY, x1, y1, x2, y2) ? HOVERED_FILLER : NORMAL_FILLER);
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture();

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT:
                initialDragLocalX = (int) mouseX - getAbsoluteX();
                initialDragLocalY = (int) mouseY - getAbsoluteY();
                break;
            case GLFW_MOUSE_BUTTON_RIGHT:
                ConnectionsPanel.mergeConnection(previous, next, this);
                break;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging()) {
            ConnectionsPanel parent = getParentWidget();
            int x = (int) mouseX - parent.getAbsoluteX() - initialDragLocalX;
            int y = (int) mouseY - parent.getAbsoluteY() - initialDragLocalY;
            setLocation(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        clearDrag();
        return true;
    }

    private void clearDrag() {
        initialDragLocalX = -1;
        initialDragLocalY = -1;
    }

    private boolean isDragging() {
        return initialDragLocalX != -1 && initialDragLocalY != -1;
    }

    @Nonnull
    @Override
    public ConnectionsPanel getParentWidget() {
        return (ConnectionsPanel) Objects.requireNonNull(super.getParentWidget());
    }

    @Nullable
    @Override
    public INode getPrevious() {
        return previous;
    }

    @Nullable
    @Override
    public INode getNext() {
        return next;
    }

    @Override
    public void connectTo(INode next) {
        this.next = next;
    }

    @Override
    public void connectFrom(INode previous) {
        this.previous = previous;
    }

    @Override
    public void disconnectNext() {
        this.next = null;
    }

    @Override
    public void disconnectPrevious() {
        this.previous = null;
    }
}
