package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import vswe.stevesfactory.api.network.INetworkController;

import java.util.Set;

/**
 * A directed pseudo-graph, with a root. The root node is the execution starting point.
 */
public interface ICommandGraph extends Iterable<IProcedure> {

    IProcedure getRoot();

    INetworkController getController();

    void execute();

    Set<IProcedure> collect();

    ICommandGraph inducedSubgraph(IProcedure node);

    CompoundNBT serialize();

    void deserialize(CompoundNBT tag);
}
