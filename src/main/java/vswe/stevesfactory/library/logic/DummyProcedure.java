package vswe.stevesfactory.library.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.graph.CommandGraph;

import javax.annotation.Nullable;

public final class DummyProcedure implements IProcedure {

    private static final ResourceLocation NAME = new ResourceLocation(StevesFactoryManager.MODID, "dummy");
    private static final IProcedure[] NEXTS_ARR = new IProcedure[0];
    private static final CommandGraph GRAPH = new CommandGraph();

    public DummyProcedure(INetworkController controller) {
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public IProcedure[] successors() {
        return NEXTS_ARR;
    }

    @Override
    public void execute(IExecutionContext context) {
    }

    @Override
    public IProcedure[] predecessors() {
        return NEXTS_ARR;
    }

    @Override
    public void linkTo(int outputIndex, IProcedure successor, int nextInputIndex) {
    }

    @Override
    public void unlink(int outputIndex) {
    }

    @Override
    public void unlink(IProcedure successor) {
    }

    @Override
    public void onLink(IProcedure predecessor, int inputIndex) {
    }

    @Override
    public void onUnlink(IProcedure predecessor) {
    }

    @Override
    public CompoundNBT serialize() {
        return new CompoundNBT();
    }

    @Override
    public void deserialize(ICommandGraph graph, CompoundNBT tag) {
    }

    @Override
    public CommandGraph getGraph() {
        return GRAPH;
    }

    @Override
    public void remove() {
    }
}
