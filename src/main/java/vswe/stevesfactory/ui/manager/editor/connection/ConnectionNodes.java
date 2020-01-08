package vswe.stevesfactory.ui.manager.editor.connection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import vswe.stevesfactory.api.logic.Connection;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;
import vswe.stevesfactory.ui.manager.editor.ConnectionsPanel;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import java.util.Map;
import java.util.function.Function;

public abstract class ConnectionNodes<N extends INode> extends AbstractContainer<N> implements ResizableWidgetMixin {

    public static ConnectionNodes<EndNode> inputNodes(int amount) {
        return new ConnectionNodes<EndNode>(amount, EndNode::new) {
            @Override
            public void readConnections(Map<IProcedure, FlowComponent<?>> m, IProcedure procedure) {
                // Only connect on output nodes
            }
        };
    }

    public static ConnectionNodes<StartNode> outputNodes(int amount) {
        return new ConnectionNodes<StartNode>(amount, StartNode::new) {
            @Override
            public void readConnections(Map<IProcedure, FlowComponent<?>> m, IProcedure procedure) {
                initializing = true;
                ImmutableList<StartNode> nodes = this.getChildren();
                Connection[] successors = procedure.successors();
                Preconditions.checkState(successors.length == nodes.size());

                for (int i = 0; i < successors.length; i++) {
                    Connection connection = successors[i];
                    if (connection == null) {
                        continue;
                    }
                    IProcedure successor = connection.getDestination();
                    FlowComponent<?> other = m.get(successor);
                    StartNode from = nodes.get(i);
                    EndNode to = other.getInputNodes().nodes.get(connection.getDestinationInputIndex());

                    ConnectionsPanel.connect(from, to);
                }
                initializing = false;
            }
        };
    }

    private final ImmutableList<N> nodes;
    protected boolean initializing = false;

    public ConnectionNodes(int amountNodes, Function<Integer, N> factory) {
        super(0, 0, 0, ConnectionsPanel.REGULAR_HEIGHT);
        ImmutableList.Builder<N> builder = ImmutableList.builder();
        for (int i = 0; i < amountNodes; i++) {
            builder.add(factory.apply(i));
        }
        this.nodes = builder.build();
    }

    @Override
    public void reflow() {
        int sections = nodes.size() + 1;
        int emptyWidth = getWidth() - nodes.size() * ConnectionsPanel.REGULAR_WIDTH;
        int sectionWidth = emptyWidth / sections;

        int x = sectionWidth;
        for (N node : nodes) {
            node.setX(x);
            x += node.getWidth() + sectionWidth;
        }
    }

    @Override
    public ImmutableList<N> getChildren() {
        return nodes;
    }

    public abstract void readConnections(Map<IProcedure, FlowComponent<?>> m, IProcedure procedure);

//    public void removeConnection(int i) {
//        nodes.get(i).disconnect();
//    }
//
//    public boolean removeConnection(N other) {
//        for (N node : nodes) {
//            if (node.getPairedNode() == other) {
//                node.disconnect();
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean removeConnection(FlowComponent<?> component) {
//        for (N node : nodes) {
//            if (node.getParentWidget().getParentWidget() == component) {
//                node.disconnect();
//                return true;
//            }
//        }
//        return false;
//    }

    public void removeAllConnections() {
        // TODO
//        for (N node : nodes) {
//            ConnectionsPanel.breakCompletely(Either.);
//        }
    }

    @Override
    public void onParentPositionChanged() {
        super.onParentPositionChanged();
        // AbstractWidget overrides the method, therefore we need to trigger this manually here9
        notifyChildrenForPositionChange();
    }
}
