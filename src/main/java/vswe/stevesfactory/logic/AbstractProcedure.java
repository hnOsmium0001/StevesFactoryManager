package vswe.stevesfactory.logic;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.api.SFMAPI;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractProcedure implements IProcedure, IProcedureClientData {

    private IProcedureType<?> type;

    private transient Connection[] successors;
    private transient Connection[] predecessors;

    private transient CommandGraph graph;

    // Client data
    private int componentX;
    private int componentY;
    private String name;

    public AbstractProcedure(IProcedureType<?> type, CommandGraph graph, int possibleParents, int possibleChildren) {
        Preconditions.checkArgument(!graph.getController().isRemoved(), "The controller object is invalid!");
        this.type = type;
        this.graph = graph;
        this.successors = new Connection[possibleChildren];
        this.predecessors = new Connection[possibleParents];
    }

    public AbstractProcedure(IProcedureType<?> type, INetworkController controller, int possibleParents, int possibleChildren) {
        this.setController(controller);
        this.type = type;
        this.successors = new Connection[possibleChildren];
        this.predecessors = new Connection[possibleParents];
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
        graph = graph.inducedSubgraph(this);
        getController().addCommandGraph(graph);
        return ret;
    }

    @Override
    public Connection removeOutputConnection(int index) {
        Connection ret = successors[index];
        setOutputConnection(null, index);
        return ret;
    }

    @Override
    public void remove() {
        for (Connection predecessor : predecessors) {
            if (predecessor != null) {
                predecessor.remove();
            }
        }
        for (Connection successor : successors) {
            if (successor != null) {
                successor.remove();
            }
        }
        if (isRoot()) {
            getController().removeCommandGraph(graph);
        }
    }

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

    @Override
    public String getName() {
        return MoreObjects.firstNonNull(name, "");
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isNameInitialized() {
        return name != null;
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
        tag.putString("Name", getName());
        return tag;
    }

    @Override
    public void deserialize(CommandGraph graph, CompoundNBT tag) {
        Preconditions.checkArgument(readType(tag) == type);
        this.graph = graph;
        componentX = tag.getInt("CompX");
        componentY = tag.getInt("CompY");
        name = tag.getString("Name");
    }

    @Override
    public ResourceLocation getRegistryName() {
        return type.getRegistryName();
    }

    protected final void pushFrame(IExecutionContext context, @Nullable Connection connection) {
        if (connection != null) {
            context.push(connection.getDestination());
        }
    }

    protected final void pushFrame(IExecutionContext context, int outputIndex) {
        pushFrame(context, successors[outputIndex]);
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("successors", successors)
                .add("predecessors", predecessors)
                .add("graph", "CommandGraph@" + graph.hashCode())
                .add("componentX", componentX)
                .add("componentY", componentY)
                .toString();
    }

    public static IProcedureType<?> readType(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return SFMAPI.getProceduresRegistry().getValue(id);
    }
}
