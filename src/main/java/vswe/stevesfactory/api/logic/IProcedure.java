package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import javax.annotation.Nonnull;
import java.util.List;

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
     * registry name as this, and results in an equivalent procedure object using {@link IProcedureType#retrieveInstance(CompoundNBT)}.
     * <p>
     * The resulting NBT should not contain connection information that are only valid based on its context ({@link CommandGraph}).
     *
     * @implSpec The resulting NBT must contain an entry with the key "{@code ID}", associated with the registry name of the procedure.
     */
    CompoundNBT serialize();

    void deserialize(CompoundNBT tag);

    // TODO make this not implementation-dependent
    @OnlyIn(Dist.CLIENT)
    FlowComponent<?> createFlowComponent();

    /**
     * Populate the given list with (potential) errors. This method should directly return the parameter {@code list} with the errors added;
     * no modification to previous existing errors should be done.
     * <p>
     * This will be used to present a list of errors to the player in the client, for example.
     *
     * @param errors The list to add errors to
     * @return The parameter {@code list} with errors added
     */
    @SuppressWarnings("UnusedReturnValue")
    @OnlyIn(Dist.CLIENT)
    List<String> populateErrors(List<String> errors);

    boolean isValid();

    CommandGraph getGraph();

    void setGraph(CommandGraph graph);

    /**
     * Remove this procedure and unlink all the related nodes.
     * <p>
     * The result of this operation should split the graph {@link #getGraph()} into {@code n+1} different graphs, where {@code n} is the
     * number of successor nodes this node have. Note that this mechanism should be handled in each of the predecessor nodes, not here.
     */
    void remove();

    void setInputConnection(@Nonnull Connection connection, int index);

    void setOutputConnection(@Nonnull Connection connection, int index);

    @SuppressWarnings("UnusedReturnValue")
    Connection removeInputConnection(int index);

    @SuppressWarnings("UnusedReturnValue")
    Connection removeOutputConnection(int index);
}
