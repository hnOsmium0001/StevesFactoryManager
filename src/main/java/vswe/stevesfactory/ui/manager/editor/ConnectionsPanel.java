package vswe.stevesfactory.ui.manager.editor;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Either;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.api.logic.Connection;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.connection.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.opengl.GL11.*;

public final class ConnectionsPanel extends DynamicWidthWidget<INode> {

    public static final int REGULAR_WIDTH = 7;
    public static final int REGULAR_HEIGHT = 6;

    public static void connect(INode first, INode second) {
        first.connectTo(second);
        second.connectFrom(first);
    }

    private static void connectAndOverride(StartNode start, EndNode end) {
        if (start.isConnected()) {
            Either<StartNode, EndNode> terminal = Either.left(start);
            StartNode start1 = terminal.map(Function.identity(), EndNode::getStart);
            EndNode end1 = terminal.map(StartNode::getEnd, Function.identity());
            // Incomplete connection, do nothing
            if (start1 != null && end1 != null) {
                breakCompletely(start1);
            }
        }
        if (end.isConnected()) {
            Either<StartNode, EndNode> terminal = Either.right(end);
            StartNode start1 = terminal.map(Function.identity(), EndNode::getStart);
            EndNode end1 = terminal.map(StartNode::getEnd, Function.identity());
            if (start1 == null || end1 == null) {
                // Incomplete connection, do nothing
                return;
            }
            breakCompletely(start1);
        }

    }

    public static void breakConnection(INode from, INode to) {
        from.disconnectNext();
        to.disconnectPrevious();
    }

    public static void breakCompletely(StartNode startNode) {
        INode nextTarget = startNode;
        while (true) {
            INode current = nextTarget;
            INode next = current.getNext();
            if (next == null) {
                break;
            }
            ConnectionsPanel.breakConnection(current, next);
            nextTarget = next;
        }
    }

    public static void breakCompletely(EndNode end) {
        INode nextTarget = end;
        while (true) {
            INode current = nextTarget;
            INode previous = current.getPrevious();
            if (previous == null) {
                break;
            }
            ConnectionsPanel.breakConnection(current, previous);
            nextTarget = previous;
        }
    }

    public static void drawConnectionLine(INode first, INode second) {
        drawConnectionLine(
                first.getAbsoluteX() - first.getWidth() / 2, first.getAbsoluteY() - first.getHeight() / 2,
                second.getAbsoluteX() - second.getWidth() / 2, second.getAbsoluteY() - second.getHeight() / 2
        );
    }

    public static void drawConnectionLine(int x1, int y1, int x2, int y2) {
        GlStateManager.disableTexture();
        glEnable(GL11.GL_LINE_SMOOTH);
        glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        glLineWidth(4);
        glColor3f(94F / 255F, 94F / 255F, 94F / 255F);
        glBegin(GL11.GL_LINES);
        {
            glVertex3f(x1, y1, 0F);
            glVertex3f(x2, y2, 0F);
        }
        glEnd();
        glColor3f(1F, 1F, 1F);
        GlStateManager.enableTexture();
    }

    private Set<INode> children = new HashSet<>();

    private Either<StartNode, EndNode> selectedNode = null;

    public ConnectionsPanel() {
        super(WidthOccupierType.MAX_WIDTH);
    }

    @Override
    public Set<INode> getChildren() {
        return children;
    }

    // This is meant to be directly in back of the EditorPanel
    @Override
    public BoxSizing getBoxSizing() {
        return BoxSizing.PHANTOM;
    }

    @Override
    public void reflow() {
        EditorPanel editor = FactoryManagerGUI.getActiveGUI().getTopLevel().editorPanel;
        setLocation(editor.getX(), editor.getY());
        setDimensions(editor.getWidth(), editor.getHeight());
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        for (INode child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
        // TODO render line
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        switch (button) {
            // No idea what's the point of this but it looks good
            case GLFW_MOUSE_BUTTON_LEFT:
            case GLFW_MOUSE_BUTTON_RIGHT:
                clearSelection();
                return true;
            default:
                return false;
        }
    }

    // Centralized logic for terminal (start and end) nodes.
    // Intermediate node logic handled within the class itself

    public void onTerminalNodeClick(Either<StartNode, EndNode> node, int button) {
        switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT: {
                handleTerminalNodeConnecting(node);
                break;
            }
            case GLFW_MOUSE_BUTTON_RIGHT: {
                handleTerminalNodeDisconnecting(node);
                break;
            }
        }
    }

    private void handleTerminalNodeConnecting(Either<StartNode, EndNode> node) {
        // Select the node if nothing is selected
        if (selectedNode == null) {
            this.selectedNode = node;
            return;
        }

        // Other wise try to connect both nodes
        StartNode start = node.map(Function.identity(), __ -> selectedNode.left().orElse(null));
        EndNode end = node.map(__ -> selectedNode.right().orElse(null), Function.identity());
        if (start == null || end == null) {
            // Invalid node pair, do nothing
            return;
        }

        // Create connection in GUI
        ConnectionsPanel.connect(start, end);
        // Create connection in actual flowchart
        Connection.createAndOverride(start.getProcedure(), start.getIndex(), end.getProcedure(), end.getIndex());
    }

    private void handleTerminalNodeDisconnecting(Either<StartNode, EndNode> node) {
        StartNode start = node.map(Function.identity(), EndNode::getStart);
        EndNode end = node.map(StartNode::getEnd, Function.identity());
        if (start == null || end == null) {
            // Incomplete connection, do nothing
            return;
        }
        ConnectionsPanel.breakCompletely(start);
    }

    public void clearSelection() {
        selectedNode = null;
    }
}
