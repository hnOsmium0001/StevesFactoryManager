package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import javax.annotation.Nonnull;

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
     *
     * @implSpec The resulting NBT must contain an entry with the key "{@code ID}", associated with the registry name of the procedure.
     */
    CompoundNBT serialize();

    void deserialize(CompoundNBT tag);

    // TODO make this implementation-independent
    @OnlyIn(Dist.CLIENT)
    FlowComponent<?> createFlowComponent();

    boolean isValid();

    /**
     * Remove this procedure and unlink all the related nodes.
     */
    void remove();

    void setInputConnection(@Nonnull Connection connection, int index);

    void setOutputConnection(@Nonnull Connection connection, int index);

    @SuppressWarnings("UnusedReturnValue")
    Connection removeInputConnection(int index);

    @SuppressWarnings("UnusedReturnValue")
    Connection removeOutputConnection(int index);
}
