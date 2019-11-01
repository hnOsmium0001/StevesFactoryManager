package vswe.stevesfactory.ui.manager.editor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.api.logic.Connection;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;
import vswe.stevesfactory.utils.VectorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.opengl.GL11.*;
import static vswe.stevesfactory.ui.manager.editor.ConnectionNodes.Node;

public abstract class ConnectionNodes extends AbstractContainer<Node> implements ResizableWidgetMixin {

    public static abstract class Node extends AbstractIconButton implements LeafWidgetMixin {

        public static void drawConnectionLine(Node first, Node second) {
            drawConnectionLine(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());
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

        public static final int WIDTH = 7;
        public static final int HEIGHT = 6;

        private Node pairedNode;
        private int index;

        protected Connection connection;

        public Node(ConnectionNodes parent, int index) {
            super(0, 0, WIDTH, HEIGHT);
            setParentWidget(parent);
            this.index = index;
        }

        /**
         * Connect to the parameter node.
         *
         * @implSpec Implementations should invoke the parameter node's {@link #onConnect(Node)} method with itself as the parameter.
         */
        public void connect(Node other) {
            // Ensure one node only ever connects to at most one other node
            // If we don't disconnect, the old connected node and the new node will both link to this node
            if (pairedNode != null) {
                pairedNode.disconnect();
            }
            pairedNode = other;
            other.onConnect(this);
        }

        /**
         * Called when {@link #connect(Node)} gets invoked on the paired node.
         */
        protected void onConnect(Node source) {
            if (pairedNode != null) {
                pairedNode.disconnect();
            }
            pairedNode = source;
        }

        public boolean shouldConnect(Node other) {
            return other.getFlowComponent() != this.getFlowComponent();
        }

        /**
         * Disconnect this node and the paired node.
         *
         * @implSpec Implementations should invoke the paired node's {@link #onDisconnect()} method.
         */
        public void disconnect() {
            if (pairedNode == null) {
                return;
            }
            pairedNode.onDisconnect();
            pairedNode = null;
        }

        /**
         * Called when {@link #disconnect()} gets invoked on the paired node.
         */
        protected void onDisconnect() {
            pairedNode = null;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isInside(mouseX, mouseY)) {
                return false;
            }
            getWindow().setFocusedWidget(this);
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    EditorPanel editor = getFlowComponent().getParentWidget();
                    // If failed to finish connection (a connection has not been started yet)
                    if (!editor.tryFinishConnection(this)) {
                        editor.startConnection(this);
                    }
                    break;
                case GLFW_MOUSE_BUTTON_RIGHT:
                    disconnect();
                    break;
            }
            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (isInside(mouseX, mouseY)) {
                getWindow().setFocusedWidget(null);
                return true;
            }
            return false;
        }

        @Nullable
        public Node getPairedNode() {
            return pairedNode;
        }

        public int getCenterX() {
            return getAbsoluteX() + getTextureNormal().getPortionWidth() / 2;
        }

        public int getCenterY() {
            return getAbsoluteY() + getTextureNormal().getPortionHeight() / 2;
        }

        public int getIndex() {
            return index;
        }

        @Nonnull
        @Override
        public ConnectionNodes getParentWidget() {
            return (ConnectionNodes) Objects.requireNonNull(super.getParentWidget());
        }

        public FlowComponent<?> getFlowComponent() {
            return (FlowComponent<?>) getParentWidget().getParentWidget();
        }

        public IProcedure getLinkedProcedure() {
            return getFlowComponent().getProcedure();
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            GlStateManager.color3f(1F, 1F, 1F);
            super.render(mouseX, mouseY, particleTicks);
        }
    }

    static final class InputNode extends Node {

        public static final TextureWrapper INPUT_NORMAL = TextureWrapper.ofFlowComponent(18, 51, WIDTH, HEIGHT);
        public static final TextureWrapper INPUT_HOVERED = INPUT_NORMAL.toRight(1);

        public InputNode(ConnectionNodes parent, int index) {
            super(parent, index);
        }

        @Override
        public void connect(Node other) {
            if (other instanceof InputNode) {
                throw new IllegalArgumentException();
            }
            super.connect(other);
        }

        @Override
        protected void onConnect(Node source) {
            if (source instanceof InputNode) {
                throw new IllegalArgumentException();
            }
            super.onConnect(source);
        }

        @Override
        public boolean shouldConnect(Node other) {
            return other instanceof OutputNode && super.shouldConnect(other);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return INPUT_NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return INPUT_HOVERED;
        }

        @Nullable
        @Override
        public OutputNode getPairedNode() {
            return (OutputNode) super.getPairedNode();
        }
    }

    static final class OutputNode extends Node {

        public static final int BORDER = 0xff4d4d4d;
        public static final int NORMAL_FILLER = 0xff9d9d9d;
        public static final int HOVERED_FILLER = 0xffc7c7c7;

        public static final int NODE_WIDTH = 6;
        public static final int NODE_HEIGHT = 6;
        public static final int HALF_NODE_WIDTH = NODE_WIDTH / 2;
        public static final int HALF_NODE_HEIGHT = NODE_HEIGHT / 2;

        public static final TextureWrapper OUTPUT_NORMAL = TextureWrapper.ofFlowComponent(18, 45, WIDTH, HEIGHT);
        public static final TextureWrapper OUTPUT_HOVERED = OUTPUT_NORMAL.toRight(1);

        private Point draggingNode;

        public OutputNode(ConnectionNodes parent, int index) {
            super(parent, index);
        }

        @Override
        public void connect(Node other) {
            if (other instanceof OutputNode) {
                throw new IllegalArgumentException();
            }
            super.connect(other);
            linkToOther(other);
        }

        @Override
        protected void onConnect(Node source) {
            if (source instanceof OutputNode) {
                throw new IllegalArgumentException();
            }
            super.onConnect(source);
            linkToOther(source);
        }

        private void linkToOther(Node other) {
            if (!getParentWidget().initializing) {
                IProcedure target = other.getLinkedProcedure();
                Connection.createAndOverride(getLinkedProcedure(), this.getIndex(), target, other.getIndex());
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (connection != null) {
                for (Point node : connection.getPolylineNodes()) {
                    int x1 = node.x - HALF_NODE_WIDTH;
                    int y1 = node.y - HALF_NODE_HEIGHT;
                    int x2 = node.x + HALF_NODE_WIDTH;
                    int y2 = node.y + HALF_NODE_HEIGHT;
                    if (VectorHelper.isInside((int) mouseX, (int) mouseY, x1, y1, x2, y2)) {
                        draggingNode = node;
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
//            if (getWindow().getFocusedWidget() != this) {
//                return false;
//            }
            if (draggingNode != null) {
                draggingNode.x = (int) mouseX;
                draggingNode.y = (int) mouseY;
                return true;
            } else if (connection != null) {
                // TODO activation check
                draggingNode = new Point(getAbsoluteX(), getAbsoluteY());
                connection.addNodeFront(draggingNode);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            draggingNode = null;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        protected void onDisconnect() {
            super.onDisconnect();
        }

        @Override
        public boolean shouldConnect(Node other) {
            return other instanceof InputNode && super.shouldConnect(other);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return OUTPUT_NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return OUTPUT_HOVERED;
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            // Connection render handled in EditorPanel
            super.render(mouseX, mouseY, particleTicks);
        }

        public void renderConnectionLine(int mouseX, int mouseY) {
            InputNode other = getPairedNode();
            if (other != null) {
                int currX = getCenterX(), currY = getCenterY();
                int nextX, nextY;
                GlStateManager.enableDepthTest();
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0F, 0F, 0.1F);
                if (connection != null) {
                    for (Point node : connection.getPolylineNodes()) {
                        nextX = node.x;
                        nextY = node.y;

                        drawConnectionLine(currX, currY, nextX, nextY);

                        GlStateManager.disableTexture();
                        Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                        int x1 = node.x - HALF_NODE_WIDTH;
                        int y1 = node.y - HALF_NODE_HEIGHT;
                        int x2 = node.x + HALF_NODE_WIDTH;
                        int y2 = node.y + HALF_NODE_HEIGHT;
                        RenderingHelper.rectVertices(x1, y1, x2, y2, BORDER);
                        RenderingHelper.rectVertices(x1 + 1, y1 + 1, x2 - 1, y2 - 1, VectorHelper.isInside(mouseX, mouseY, x1, y1, x2, y2) ? HOVERED_FILLER : NORMAL_FILLER);
                        Tessellator.getInstance().draw();
                        GlStateManager.enableTexture();

                        currX = nextX;
                        currY = nextY;
                    }
                }
                drawConnectionLine(currX, currY, other.getCenterX(), other.getCenterY());
                GlStateManager.popMatrix();
                GlStateManager.disableDepthTest();
            }
        }

        @Nullable
        @Override
        public InputNode getPairedNode() {
            return (InputNode) super.getPairedNode();
        }
    }

    public static ConnectionNodes inputNodes(int amount) {
        return new ConnectionNodes(amount, InputNode::new) {
            @Override
            void readConnections(Map<IProcedure, FlowComponent<?>> m, IProcedure procedure) {
                // Only connect on output nodes
            }
        };
    }

    public static ConnectionNodes outputNodes(int amount) {
        return new ConnectionNodes(amount, OutputNode::new) {
            @Override
            void readConnections(Map<IProcedure, FlowComponent<?>> m, IProcedure procedure) {
                initializing = true;
                ImmutableList<Node> nodes = getChildren();
                Connection[] successors = procedure.successors();
                Preconditions.checkState(successors.length == nodes.size());

                for (int i = 0; i < successors.length; i++) {
                    Connection connection = successors[i];
                    if (connection == null) {
                        continue;
                    }
                    IProcedure successor = connection.getDestination();
                    FlowComponent<?> other = m.get(successor);
                    OutputNode from = (OutputNode) nodes.get(i);
                    Node to = other.getInputNodes().nodes.get(connection.getDestinationInputIndex());

                    from.connect(to);
                    from.connection = connection;
                }
                initializing = false;
            }
        };
    }

    private final ImmutableList<Node> nodes;
    protected boolean initializing = false;

    public ConnectionNodes(int amountNodes, BiFunction<ConnectionNodes, Integer, ? extends Node> factory) {
        super(0, 0, 0, Node.HEIGHT);
        ImmutableList.Builder<Node> builder = ImmutableList.builder();
        for (int i = 0; i < amountNodes; i++) {
            builder.add(factory.apply(this, i));
        }
        this.nodes = builder.build();
    }

    @Override
    public void reflow() {
        int sections = nodes.size() + 1;
        int emptyWidth = getWidth() - nodes.size() * Node.WIDTH;
        int sectionWidth = emptyWidth / sections;

        int x = sectionWidth;
        for (Node node : nodes) {
            node.setX(x);
            x += Node.WIDTH + sectionWidth;
        }
    }

    @Override
    public ImmutableList<Node> getChildren() {
        return nodes;
    }

    abstract void readConnections(Map<IProcedure, FlowComponent<?>> m, IProcedure procedure);

    public void removeConnection(int i) {
        nodes.get(i).disconnect();
    }

    public boolean removeConnection(Node other) {
        for (Node node : nodes) {
            if (node.getPairedNode() == other) {
                node.disconnect();
                return true;
            }
        }
        return false;
    }

    public boolean removeConnection(FlowComponent<?> component) {
        for (Node node : nodes) {
            if (node.getParentWidget().getParentWidget() == component) {
                node.disconnect();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Node child : getChildren()) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Node child : getChildren()) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Node child : getChildren()) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    public void removeAllConnections() {
        for (Node node : nodes) {
            node.disconnect();
        }
    }

    @Override
    public void onParentPositionChanged() {
        super.onParentPositionChanged();
        // AbstractWidget overrides the method, therefore we need to trigger this manually here9
        notifyChildrenForPositionChange();
    }
}
