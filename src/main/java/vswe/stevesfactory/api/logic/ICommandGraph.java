package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;

import java.util.Set;

public interface ICommandGraph extends Iterable<IProcedure> {

    IProcedure getRoot();

    void execute();

    Set<IProcedure> collect();

    ICommandGraph inducedSubgraph(IProcedure node);

    CompoundNBT serialize();

    void deserialize(CompoundNBT tag);
}
