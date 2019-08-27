package vswe.stevesfactory.library.logic;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.api.SFMAPI;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;

import java.util.Objects;

public abstract class AbstractProcedure implements IProcedure, IProcedureClientData {

    private IProcedureType<?> type;

    //    private IProcedure[] successors;
//    private IProcedure[] predecessors;
    private Connection[] successors;
    private Connection[] predecessors;

    private transient CommandGraph graph;

    // Client data
    private int componentX;
    private int componentY;

    public AbstractProcedure(IProcedureType<?> type, CommandGraph graph, int possibleParents, int possibleChildren) {
        Preconditions.checkArgument(!graph.getController().isRemoved(), "The controller object is invalid!");
        this.type = type;
        this.graph = graph;
//        this.successors = new IProcedure[possibleChildren];
//        this.predecessors = new IProcedure[possibleParents];
        this.successors = new Connection[possibleChildren];
        this.predecessors = new Connection[possibleChildren];
    }

    public AbstractProcedure(IProcedureType<?> type, INetworkController controller, int possibleParents, int possibleChildren) {
        this.setController(controller);
        this.type = type;
//        this.successors = new IProcedure[possibleChildren];
//        this.predecessors = new IProcedure[possibleParents];
        this.successors = new Connection[possibleChildren];
        this.predecessors = new Connection[possibleChildren];
    }

    public INetworkController getController() {
        INetworkController controller = graph.getController();
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");
        return controller;
    }

    public void setController(INetworkController controller) {
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");

        if (graph != null) {
            INetworkController oldController = graph.getController();
            if (oldController != null) {
                oldController.removeCommandGraph(graph);
            }
        }

        this.graph = new CommandGraph(controller, this);
        controller.addCommandGraph(graph);
    }

//    @Override
//    public IProcedure[] successors() {
//        return successors;
//    }
//
//    @Override
//    public IProcedure[] predecessors() {
//        return predecessors;
//    }

    @Override
    public Connection[] successors() {
        return successors;
    }

    @Override
    public Connection[] predecessors() {
        return predecessors;
    }

    @Override
    public void setInputConnection(Connection connection, int index) {
        predecessors[index] = connection;
        if (connection.getSource().getGraph() != this.graph && isRoot()) {
            getController().removeCommandGraph(graph);
            graph = connection.getSource().getGraph();
        }
    }

    @Override
    public void setOutputConnection(Connection connection, int index) {
        successors[index] = connection;
    }

    @Override
    public Connection removeInputConnection(int index) {
        Connection ret = predecessors[index];
        predecessors[index] = null;
        getController().removeCommandGraph(graph);
        graph = new CommandGraph(this);
        getController().addCommandGraph(graph);
        return ret;
    }

    @Override
    public Connection removeOutputConnection(int index) {
        Connection ret = successors[index];
        setOutputConnection(null, index);
        return ret;
    }

    // TODO better graph code

//    @Override
//    public void linkTo(int outputIndex, IProcedure successor, int nextInputIndex) {
//        unlink(outputIndex);
//
//        successors[outputIndex] = successor;
//        successor.onLink(this, nextInputIndex);
//    }
//
//    @Override
//    public void unlink(int outputIndex) {
//        IProcedure oldChild = successors[outputIndex];
//        if (oldChild != null) {
//            oldChild.onUnlink(this);
//            successors[outputIndex] = null;
//        }
//    }
//
//    @Override
//    public void unlink(IProcedure successor) {
//        for (int i = 0; i < successors.length; i++) {
//            if (successors[i] == successor) {
//                unlink(i);
//            }
//        }
//    }
//
//    @Override
//    public void onLink(IProcedure predecessor, int inputIndex) {
//        INetworkController controller = getController();
//
//        // In case this node is the root, remove the old graph because we are joining another graph
//        if (isRoot()) {
//            controller.removeCommandGraph(graph);
//        } else {
//            // If this is not a root node, means this node has a predecessor, or oldParent != null
//            IProcedure oldParent = predecessors[inputIndex];
//            // FIXME on deserialization no links will be present
//            if(oldParent != null) {
////                Preconditions.checkState(oldParent != null, "Encountered a non-root graph node has no predecessor!");
//
//                oldParent.unlink(this);
//
//                // During unlink, we created a new graph with this node as the root (see onUnlink)
//                // However since we are linking to another predecessor node, that graph is invalid since having a predecessor means this node will not be the root
//                Preconditions.checkState(isRoot(), "Unlinking this from a predecessor did not call onUnlink on this!");
//                controller.removeCommandGraph(graph);
//            }
//        }
//
//        predecessors[inputIndex] = predecessor;
//        graph = predecessor.getGraph();
//    }
//
//    @Override
//    public void onUnlink(IProcedure predecessor) {
//        for (int i = 0; i < predecessors.length; i++) {
//            if (predecessors[i] == predecessor) {
//                predecessors[i] = null;
//            }
//        }
//        graph = graph.inducedSubgraph(this);
//        getController().addCommandGraph(graph);
//    }
//
//
//    @Override
//    public void remove() {
//        for (IProcedure predecessor : predecessors) {
//            if (predecessor != null) {
//                predecessor.unlink(this);
//            }
//        }
//        for (int i = 0; i < successors.length; i++) {
//            unlink(i);
//        }
//        if (isRoot()) {
//            getController().removeCommandGraph(graph);
//        }
//    }

    public boolean isRoot() {
        return graph.getRoot() == this;
    }

    @Override
    public IProcedureType<?> getType() {
        return type;
    }

    @Override
    public CommandGraph getGraph() {
        return graph;
    }

    @Override
    public int getComponentX() {
        return componentX;
    }

    @Override
    public void setComponentX(int componentX) {
        this.componentX = componentX;
    }

    @Override
    public int getComponentY() {
        return componentY;
    }

    @Override
    public void setComponentY(int componentY) {
        this.componentY = componentY;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote The default implementation of this method has the ID entry written. Unless child implementations have a special need,
     * reusing this method stub is sufficient.
     */
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ID", getRegistryName().toString());
        tag.putInt("CompX", componentX);
        tag.putInt("CompY", componentY);
        return tag;
    }

    @Override
    public void deserialize(CommandGraph graph, CompoundNBT tag) {
        Preconditions.checkArgument(readType(tag) == type);
        this.graph = graph;
        componentX = tag.getInt("CompX");
        componentY = tag.getInt("CompY");
    }

    @Override
    public ResourceLocation getRegistryName() {
        return type.getRegistryName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractProcedure that = (AbstractProcedure) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public static IProcedureType<?> readType(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return SFMAPI.getProceduresRegistry().getValue(id);
    }
}
