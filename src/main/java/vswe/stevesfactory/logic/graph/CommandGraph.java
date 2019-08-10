package vswe.stevesfactory.logic.graph;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.logic.ICommandGraph;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.execution.ProcedureExecutor;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class CommandGraph implements ICommandGraph {

    private INetworkController controller;
    private IProcedure root;

    public CommandGraph(INetworkController controller, IProcedure root) {
        this.controller = controller;
        this.root = root;
    }

    public CommandGraph(INetworkController controller) {
        this.controller = controller;
    }

    public CommandGraph(IProcedure root) {
        this.root = root;
    }

    public CommandGraph() {
    }

    @Override
    public IProcedure getRoot() {
        return root;
    }

    @Override
    public INetworkController getController() {
        return controller;
    }

    @Override
    public void execute() {
        new ProcedureExecutor(controller, controller.getWorld()).start(root);
    }

    @Override
    public Set<IProcedure> collect() {
        Set<IProcedure> result = new HashSet<>();
        result.add(root);
        dfs(result, root);
        return result;
    }

    @Nonnull
    @Override
    public Iterator<IProcedure> iterator() {
        return collect().iterator();
    }

    private Set<IProcedure> dfsCollectNoRoot() {
        Set<IProcedure> result = new HashSet<>();
        dfs(result, root);
        return result;
    }

    private void dfs(Set<IProcedure> result, IProcedure node) {
        for (IProcedure next : node.successors()) {
            if (result.contains(next) || next == null) {
                continue;
            }
            result.add(next);
            dfs(result, next);
        }
    }

    @Override
    public CommandGraph inducedSubgraph(IProcedure node) {
        return new CommandGraph(controller, node);
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();

        tag.putString("Dimension", controller.getDimension().getRegistryName().toString());
        tag.put("ControllerPos", NBTUtil.writeBlockPos(controller.getPos()));

        Object2IntMap<IProcedure> idMap = createIDMap();
        ListNBT list = new ListNBT();
        for (IProcedure node : idMap.keySet()) {
            list.add(serializeNode(node, idMap));
        }
        // This is for compatibility reasons
        // If we ever need to make root not the first element, this might become useful
        tag.putInt("RootIndex", 0);
        tag.put("Nodes", list);

        return tag;
    }

    private Object2IntMap<IProcedure> createIDMap() {
        Set<IProcedure> nodes = dfsCollectNoRoot();
        Object2IntMap<IProcedure> idMap = new Object2IntLinkedOpenHashMap<>(nodes.size() + 1);
        idMap.put(root, 0);
        for (IProcedure node : nodes) {
            idMap.put(node, idMap.size());
        }
        return idMap;
    }

    private CompoundNBT serializeNode(IProcedure node, Object2IntMap<IProcedure> idMap) {
        CompoundNBT tag = new CompoundNBT();

        IProcedure[] successors = node.successors();
        int[] children = new int[successors.length];
        for (int i = 0; i < successors.length; i++) {
            IProcedure successor = successors[i];
            if (successor == null) {
                children[i] = -1;
                continue;
            }

            int id = idMap.getOrDefault(successor, -1);
            if (id == -1) {
                throw new IllegalArgumentException();
            }
            children[i] = id;
        }

        tag.put("NodeData", node.serialize());
        tag.putIntArray("Children", children);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        DimensionType dimension = DimensionType.byName(new ResourceLocation(tag.getString("Dimension")));
        BlockPos pos = NBTUtil.readBlockPos(tag.getCompound("ControllerPos"));
        controller = readController(dimension, pos);

        // First deserialize all the nodes themselves which makes it easier to set the connections
        ListNBT nodesNBT = tag.getList("Nodes", Constants.NBT.TAG_COMPOUND);
        List<IProcedure> nodes = new ArrayList<>();
        for (int i = 0; i < nodesNBT.size(); i++) {
            CompoundNBT nodeNBT = nodesNBT.getCompound(i);
            nodes.add(deserializeNode(nodeNBT));
        }

        // Set child connection to all of the nodes
        assert nodesNBT.size() == nodes.size();
        for (int i = 0; i < nodes.size(); i++) {
            IProcedure node = nodes.get(i);
            CompoundNBT nodeNBT = nodesNBT.getCompound(i);
            retrieveConnections(nodes, node, nodeNBT);
        }

        int rootID = tag.getInt("RootIndex");
        this.root = nodes.get(rootID);
    }

    private IProcedure deserializeNode(CompoundNBT nodeNBT) {
        return NetworkHelper.retrieveProcedure(this, nodeNBT.getCompound("NodeData"));
    }

    private void retrieveConnections(List<IProcedure> nodes, IProcedure node, CompoundNBT nodeNBT) {
        int[] children = nodeNBT.getIntArray("Children");
        for (int i = 0; i < children.length; i++) {
            int index = children[i];
            if (index == -1) {
                node.successors()[i] = null;
            } else {
                node.successors()[i] = nodes.get(index);
            }
        }
    }

    public static CommandGraph deserializeFrom(CompoundNBT tag) {
        CommandGraph tree = new CommandGraph();
        tree.deserialize(tag);
        return tree;
    }

    public static INetworkController readController(DimensionType dimensionType, BlockPos pos) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return (INetworkController) server.getWorld(dimensionType).getTileEntity(pos);
        }
        return (INetworkController) Minecraft.getInstance().world.getTileEntity(pos);
    }
}
