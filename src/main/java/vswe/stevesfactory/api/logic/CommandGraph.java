package vswe.stevesfactory.api.logic;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.execution.ProcedureExecutor;
import vswe.stevesfactory.utils.NetworkHelper;
import vswe.stevesfactory.utils.Utils;

import javax.annotation.Nonnull;
import java.util.*;

public class CommandGraph implements Iterable<IProcedure> {

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

    public IProcedure getRoot() {
        return root;
    }

    public void setRoot(IProcedure root) {
        this.root = root;
    }

    public INetworkController getController() {
        return controller;
    }

    public void execute() {
        new ProcedureExecutor(controller, controller.getWorld()).start(root);
    }

    public Set<IProcedure> collect() {
        Set<IProcedure> result = new HashSet<>();
        if (root != null) {
            result.add(root);
            dfs(result, root);
        }
        return result;
    }

    @Nonnull
    @Override
    public Iterator<IProcedure> iterator() {
        return collect().iterator();
    }

    private Set<IProcedure> dfsCollectNoRoot() {
        Set<IProcedure> result = new HashSet<>();
        if (root != null) {
            dfs(result, root);
        }
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

    public CommandGraph inducedSubgraph(IProcedure node) {
        return new CommandGraph(controller, node);
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();

        tag.putString("Dimension", Objects.requireNonNull(controller.getDimension().getRegistryName()).toString());
        tag.put("ControllerPos", NBTUtil.writeBlockPos(controller.getPos()));

        Object2IntMap<IProcedure> idMap = createIDMap();
        ListNBT list = new ListNBT();
        int i = 0;
        for (Object2IntMap.Entry<IProcedure> entry : idMap.object2IntEntrySet()) {
            IProcedure node = entry.getKey();
            int id = entry.getIntValue();
            Validate.isTrue(id == i);
            list.add(serializeNode(node, idMap));
            i++;
        }
        // This is for compatibility reasons
        // If we ever need to make root not the first element, this might become useful
        tag.putInt("RootID", 0);
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
            } else {
                int targetID = idMap.getOrDefault(successor, -1);
                Validate.isTrue(targetID != -1);
                // Input index is stored as the actualIndex + 1 to avoid dealing with complement bits
                // On deserialization, impl should -1 from the index before use
                int nextInputIndex = ArrayUtils.indexOf(successor.predecessors(), node);
                children[i] = (nextInputIndex + 1) << 16 | targetID;
            }
        }

//        IProcedure[] predecessors = node.predecessors();
//        int[] parents = new int[predecessors.length];
//        for (int i = 0; i < predecessors.length; i++) {
//            IProcedure predecessor = predecessors[i];
//            int k = idMap.getOrDefault(predecessor, -1);
//            parents[i] = k;
//        }

        tag.put("NodeData", node.serialize());
        tag.putIntArray("Children", children);
//        tag.putIntArray("Parents", parents);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        DimensionType dimension = Objects.requireNonNull(DimensionType.byName(new ResourceLocation(tag.getString("Dimension"))));
        BlockPos pos = NBTUtil.readBlockPos(tag.getCompound("ControllerPos"));
        controller = (INetworkController) Utils.getWorldForSide(dimension).getTileEntity(pos);

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

        int rootID = tag.getInt("RootID");
        this.root = nodes.get(rootID);
    }

    private IProcedure deserializeNode(CompoundNBT nodeNBT) {
        return NetworkHelper.retrieveProcedure(this, nodeNBT.getCompound("NodeData"));
    }

    private void retrieveConnections(List<IProcedure> nodes, IProcedure node, CompoundNBT nodeNBT) {
        int[] children = nodeNBT.getIntArray("Children");
        for (int i = 0; i < children.length; i++) {
            int idData = children[i];
            if (idData != -1) {
                IProcedure target = Validate.notNull(nodes.get(idData & 0xffff));
                // Index is stored in actualIndex + 1
                // See serialization logic
                int nextInputIndex = (idData >>> 16) - 1;
                if (nextInputIndex != -1) {
                    node.linkTo(i, target, nextInputIndex);
                }
            }
        }

//        int[] parents = nodeNBT.getIntArray("Parents");
//        for (int i = 0; i < parents.length; i++) {
//            int index = parents[i];
//            if (index != -1) {
//                IProcedure target = nodes.get(index);
//                target.linkTo(i >>> 16, node, i & 16);
//            }
//        }
    }

    public static CommandGraph deserializeFrom(CompoundNBT tag) {
        CommandGraph tree = new CommandGraph();
        tree.deserialize(tag);
        return tree;
    }
}
