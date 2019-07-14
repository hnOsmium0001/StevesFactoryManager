package vswe.stevesfactory.blocks.manager.components;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static vswe.stevesfactory.blocks.manager.components.ControlFlowNodes.Node;

public class ControlFlowNodes extends AbstractWidget implements IContainer<Node>, ContainerWidgetMixin<Node>, RelocatableContainerMixin<Node>, ResizableWidgetMixin {

    public static abstract class Node extends AbstractIconButton implements IWidget, LeafWidgetMixin, RelocatableWidgetMixin {

        public static final int WIDTH = 7;
        public static final int HEIGHT = 6;

        public Node() {
            super(0, 0, WIDTH, HEIGHT);
        }
    }

    private static final class InputNode extends Node {

        private static final TextureWrapper INPUT_NORMAL = TextureWrapper.ofFlowComponent(0, 58, WIDTH, HEIGHT);
        private static final TextureWrapper INPUT_HOVERED = TextureWrapper.ofFlowComponent(7, 58, WIDTH, HEIGHT);

        @Override
        public TextureWrapper getTextureNormal() {
            return INPUT_NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return INPUT_HOVERED;
        }
    }

    private static final class OutputNode extends Node {

        private static final TextureWrapper OUTPUT_NORMAL = TextureWrapper.ofFlowComponent(0, 64, WIDTH, HEIGHT);
        private static final TextureWrapper OUTPUT_HOVERED = TextureWrapper.ofFlowComponent(7, 64, WIDTH, HEIGHT);

        @Override
        public TextureWrapper getTextureNormal() {
            return OUTPUT_NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return OUTPUT_HOVERED;
        }
    }

    public static ControlFlowNodes inputNodes(FlowComponent parent, int amount) {
        return new ControlFlowNodes(parent, amount, InputNode::new);
    }

    public static ControlFlowNodes outputNodes(FlowComponent parent, int amount) {
        return new ControlFlowNodes(parent, amount, OutputNode::new);
    }

    private final List<Node> nodes;

    public ControlFlowNodes(FlowComponent parent, int amountNodes, Supplier<? extends Node> supplier) {
        super(0, 7);
        onParentChanged(parent);
        onStateChanged(parent.getState());
        this.nodes = Stream.generate(supplier).limit(amountNodes).collect(ImmutableList.toImmutableList());
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

    public void onStateChanged(FlowComponent.State newState) {
        setWidth(newState.dimensions.width);
        reflow();
    }

    @Override
    public Collection<Node> getChildren() {
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
}
