package vswe.stevesfactory.ui.manager.components;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static vswe.stevesfactory.ui.manager.components.ControlFlowNodes.Node;

public class ControlFlowNodes extends AbstractWidget implements IContainer<Node>, ContainerWidgetMixin<Node>, RelocatableContainerMixin<Node>, ResizableWidgetMixin {

    public static abstract class Node extends AbstractIconButton implements IWidget, LeafWidgetMixin, RelocatableWidgetMixin {

        public static void drawConnectionLine(Node first, Node second) {
            drawConnectionLine(first.getCenterX(), first.getCenterY(), second.getCenterX(), second.getCenterY());
        }

        public static void drawConnectionLine(Node source, int mx, int my) {
            drawConnectionLine(source.getCenterX(), source.getCenterY(), mx, my);
        }

        public static void drawConnectionLine(int x1, int y1, int x2, int y2) {
            // TODO make connection lines render under all flow components
            // depth doesn't seem to be working
            GlStateManager.enableDepthTest();
            GlStateManager.disableTexture();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(6);
            GL11.glColor3f(94F / 255F, 94F / 255F, 94F / 255F);
            GL11.glBegin(GL11.GL_LINES);
            {
                GL11.glVertex3f(x1, y1, 0F);
                GL11.glVertex3f(x2, y2, 0F);
            }
            GL11.glEnd();
            GL11.glColor3f(1F, 1F, 1F);
            GlStateManager.enableTexture();
        }

        public static final int WIDTH = 7;
        public static final int HEIGHT = 6;

        private Node pairedNode;

        public Node(ControlFlowNodes parent) {
            super(0, 0, WIDTH, HEIGHT);
            onParentChanged(parent);
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
            return other.getParentFlowComponent() != this.getParentFlowComponent();
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
            switch (button) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                    EditorPanel editor = getParentFlowComponent().getParentWidget();
                    // If failed to finish connection (a connection has not been started yet)
                    if (!editor.tryFinishConnection(this)) {
                        editor.startConnection(this);
                    }
                    break;
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                    disconnect();
                    break;
            }
            return true;
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

        @Nonnull
        @Override
        public ControlFlowNodes getParentWidget() {
            return (ControlFlowNodes) Objects.requireNonNull(super.getParentWidget());
        }

        public FlowComponent getParentFlowComponent() {
            return getParentWidget().getParentWidget();
        }
    }

    private static final class InputNode extends Node {

        private static final TextureWrapper INPUT_NORMAL = TextureWrapper.ofFlowComponent(0, 64, WIDTH, HEIGHT);
        private static final TextureWrapper INPUT_HOVERED = TextureWrapper.ofFlowComponent(7, 64, WIDTH, HEIGHT);


        public InputNode(ControlFlowNodes parent) {
            super(parent);
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

    private static final class OutputNode extends Node {

        private static final TextureWrapper OUTPUT_NORMAL = TextureWrapper.ofFlowComponent(0, 58, WIDTH, HEIGHT);
        private static final TextureWrapper OUTPUT_HOVERED = TextureWrapper.ofFlowComponent(7, 58, WIDTH, HEIGHT);

        public OutputNode(ControlFlowNodes parent) {
            super(parent);
        }

        @Override
        public void connect(Node other) {
            if (other instanceof OutputNode) {
                throw new IllegalArgumentException();
            }
            super.connect(other);
        }

        @Override
        protected void onConnect(Node source) {
            if (source instanceof OutputNode) {
                throw new IllegalArgumentException();
            }
            super.onConnect(source);
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
            InputNode other = getPairedNode();
            if (other != null) {
                drawConnectionLine(this, other);
            }
            super.render(mouseX, mouseY, particleTicks);
        }

        @Nullable
        @Override
        public InputNode getPairedNode() {
            return (InputNode) super.getPairedNode();
        }
    }

    public static ControlFlowNodes inputNodes(FlowComponent parent, int amount) {
        return new ControlFlowNodes(parent, amount, InputNode::new);
    }

    public static ControlFlowNodes outputNodes(FlowComponent parent, int amount) {
        return new ControlFlowNodes(parent, amount, OutputNode::new);
    }

    private final ImmutableList<Node> nodes;

    public ControlFlowNodes(FlowComponent parent, int amountNodes, Function<ControlFlowNodes, ? extends Node> factory) {
        super(0, Node.HEIGHT);
        onParentChanged(parent);
        this.nodes = Stream.generate(() -> factory.apply(this)).limit(amountNodes).collect(ImmutableList.toImmutableList());
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

    public void updateTo(FlowComponent.State newState) {
        setWidth(newState.dimensions.width);
        reflow();
    }

    @Override
    public ImmutableList<Node> getChildren() {
        return nodes;
    }

    @Override
    public IContainer<Node> addChildren(Node widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer<Node> addChildren(Collection<Node> widgets) {
        throw new UnsupportedOperationException();
    }

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

    public boolean removeConnection(FlowComponent component) {
        for (Node node : nodes) {
            if (node.getParentWidget().getParentWidget() == component) {
                node.disconnect();
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

    @Nonnull
    @Override
    public FlowComponent getParentWidget() {
        return (FlowComponent) Objects.requireNonNull(super.getParentWidget());
    }
}
