package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

public interface IProcedure {

    IProcedureType<?> getType();

    ResourceLocation getRegistryName();

    /**
     * Get an array of successor nodes. A null element represents a empty, but possible edge that can be created. If all elements in the
     * returned array are null, this node is the root of the graph.
     */
    Connection[] successors();

    /**
     * Get an array of predecessor nodes. A null element represents a empty, but possible edge that can be created.
     */
    Connection[] predecessors();

    /**
     * Execute this procedure, and push the next procedure the control flow should go to.
     */
    void execute(IExecutionContext context);

    /**
     * Serialize the procedure into a retrievable NBT format. This NBT compound should be able to be put into any factory with the same
     * registry name as this, and results in an equivalent procedure object using {@link IProcedureType#retrieveInstance(CommandGraph,
     * CompoundNBT)}.
     *
     * @implSpec The resulting NBT must contain an entry with the key "{@code ID}", associated with the registry name of the procedure.
     */
    CompoundNBT serialize();

    void deserialize(CommandGraph graph, CompoundNBT tag);

    FlowComponent<?> createFlowComponent();

    CommandGraph getGraph();

    /**
     * Remove this procedure and unlink all the related nodes.
     * <p>
     * The result of this operation should split the graph {@link #getGraph()} into {@code n+1} different graphs, where {@code n} is the
     * number of predecessor nodes this node have. Note that this mechanism should be handled in each of the predecessor nodes, not here.
     */
    void remove();

    void setInputConnection(Connection connection, int index);

    void setOutputConnection(Connection connection, int index);

    @SuppressWarnings("UnusedReturnValue")
    Connection removeInputConnection(int index);

    @SuppressWarnings("UnusedReturnValue")
    Connection removeOutputConnection(int index);

//    /**
//     * Create a directed edge from this node to the parameter {@code successor}.
//     *
//     * @param outputIndex    Index in {@link #successors()} to put a link to {@code successor}.
//     * @param successor      Target node to connect to
//     * @param nextInputIndex Index in {@link #predecessors()} of {@code successor} to put a link to this node.
//     * @implSpec This method should call {@link #onLink(IProcedure, int)} on {@code successor} with the parameters {@code (this,
//     * nextInputIndex)}. Additionally implementation may unlink the original link at {@code outputIndex} first.
//     */
//    void linkTo(int outputIndex, IProcedure successor, int nextInputIndex);
//
//    /**
//     * Clear the edge on {@code outputIndex}.
//     *
//     * @param outputIndex Index in {@link #successors()} to remove
//     */
//    void unlink(int outputIndex);
//
//    /**
//     * Find the index of the successor {@code successor}, and unlink the found node. This method should call {@link #unlink(int)} after the
//     * connection has been found.
//     *
//     * @see #unlink(int)
//     */
//    void unlink(IProcedure successor);
//
//    /**
//     * Called when a edge from {@code predecessor} to {@code this} node has been added in this graph.
//     * <p>
//     * This node is in charge of updating graph references in the associated controller.
//     *
//     * @param predecessor The predecessor that has linked this node.
//     * @param inputIndex  Index in {@link #predecessors()} that should be updated to {@code predecessor}
//     */
//    void onLink(IProcedure predecessor, int inputIndex);
//
//    /**
//     * Called when a edge from {@code predecessor} to {@code this} node has been removed in this graph.
//     * <p>
//     * This node is in charge of updating graph references in the associated controler.
//     *
//     * @param predecessor The predecessor that has unlinked this node.
//     */
//    void onUnlink(IProcedure predecessor);
}
