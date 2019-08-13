package vswe.stevesfactory.library.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

public final class DummyProcedure implements IProcedure, IProcedureDataStorage {

    private static final ResourceLocation NAME = new ResourceLocation(StevesFactoryManager.MODID, "dummy");
    private static final IProcedureType<DummyProcedure> TYPE = new SimpleProcedureType<>(DummyProcedure::new, NAME);
    private static final IProcedure[] NEXTS_ARR = new IProcedure[0];
    private static final CommandGraph GRAPH = new CommandGraph();

    public DummyProcedure(INetworkController controller) {
    }

    @Override
    public IProcedureType<?> getType() {
        return TYPE;
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
    public void deserialize(CommandGraph graph, CompoundNBT tag) {
    }

    @Override
    public FlowComponent<?> createFlowComponent() {
        return FlowComponent.of(this);
    }

    @Override
    public CommandGraph getGraph() {
        return GRAPH;
    }

    @Override
    public void remove() {
    }

    @Override
    public int getComponentX() {
        return 0;
    }

    @Override
    public void setComponentX(int x) {
    }

    @Override
    public int getComponentY() {
        return 0;
    }

    @Override
    public void setComponentY(int y) {
    }
}
