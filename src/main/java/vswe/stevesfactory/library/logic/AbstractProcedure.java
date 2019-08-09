package vswe.stevesfactory.library.logic;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.SFMAPI;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.graph.CommandGraph;

public abstract class AbstractProcedure implements IProcedure {

    private IProcedureType<?> type;

    private INetworkController controller;
    private IProcedure[] successors;
    private IProcedure[] predecessors;

    private transient ICommandGraph graph;

    public AbstractProcedure(IProcedureType<?> type, INetworkController controller, int possibleParents, int possibleChildren) {
        this.type = type;
        this.setController(controller);
        this.successors = new IProcedure[possibleChildren];
        this.predecessors = new IProcedure[possibleParents];
    }

    public INetworkController getController() {
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");
        return controller;
    }

    public void setController(INetworkController controller) {
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");

        INetworkController oldController = this.controller;
        if (oldController != null) {
            oldController.removeCommandGraph(graph);
        }

        this.controller = controller;
        this.graph = new CommandGraph(controller, this);
        controller.addCommandGraph(graph);
    }

    @Override
    public IProcedure[] successors() {
        return successors;
    }

    @Override
    public IProcedure[] predecessors() {
        return predecessors;
    }

    // TODO better graph code

    @Override
    public void linkTo(int outputIndex, IProcedure successor, int nextInputIndex) {
        unlink(outputIndex);

        successors[outputIndex] = successor;
        successor.onLink(this, nextInputIndex);
    }

    @Override
    public void unlink(int outputIndex) {
        IProcedure oldChild = successors[outputIndex];
        if (oldChild != null) {
            oldChild.onUnlink(this);
            successors[outputIndex] = null;
        }
    }

    @Override
    public void unlink(IProcedure successor) {
        for (int i = 0; i < successors.length; i++) {
            if (successors[i] == successor) {
                unlink(i);
            }
        }
    }

    @Override
    public void onLink(IProcedure predecessor, int inputIndex) {
        // In case this node is the root, remove the old graph because we are joining another graph
        if (isRoot()) {
            controller.removeCommandGraph(graph);
        } else {
            // If this is not a root node, means this node has a predecessor, or oldParent != null
            IProcedure oldParent = predecessors[inputIndex];
            Preconditions.checkState(oldParent != null, "Encountered a non-root graph node has no predecessor!");

            oldParent.unlink(this);

            // During unlink, we created a new graph with this node as the root (see onUnlink)
            // However since we are linking to another predecessor node, that graph is invalid since having a predecessor means this node will not be the root
            Preconditions.checkState(isRoot(), "Unlinking this from a predecessor did not call onUnlink on this!");
            controller.removeCommandGraph(graph);
        }

        predecessors[inputIndex] = predecessor;
        graph = predecessor.getGraph();
    }

    @Override
    public void onUnlink(IProcedure predecessor) {
        for (int i = 0; i < predecessors.length; i++) {
            if (predecessors[i] == predecessor) {
                predecessors[i] = null;
            }
        }
        graph = graph.inducedSubgraph(this);
        controller.addCommandGraph(graph);
    }

    public boolean isRoot() {
        return graph.getRoot() == this;
    }

    @Override
    public void remove() {
        for (IProcedure predecessor : predecessors) {
            predecessor.unlink(this);
        }
        for (int i = 0; i < successors.length; i++) {
            unlink(i);
        }
    }

    public IProcedureType<?> getType() {
        return type;
    }

    @Override
    public ICommandGraph getGraph() {
        return graph;
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
        tag.putString("DimensionType", controller.getDimension().toString());
        tag.put("ControllerPos", NBTUtil.writeBlockPos(controller.getPos()));
        return tag;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return type.getRegistryName();
    }

    public static IProcedureType<?> readType(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return SFMAPI.getProceduresRegistry().getValue(id);
    }

    public static BlockPos readControllerPos(CompoundNBT tag) {
        return NBTUtil.readBlockPos(tag.getCompound("ControllerPos"));
    }

    public static DimensionType raedDimensionType(CompoundNBT tag) {
        return DimensionType.byName(new ResourceLocation(tag.getString("Dimension")));
    }

    public static INetworkController readController(CompoundNBT tag) {
        return readController(raedDimensionType(tag), readControllerPos(tag));
    }

    public static INetworkController readController(DimensionType dimensionType, BlockPos pos) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return (INetworkController) server.getWorld(dimensionType).getTileEntity(pos);
        }
        return (INetworkController) Minecraft.getInstance().world.getTileEntity(pos);
    }
}
