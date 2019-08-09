package vswe.stevesfactory.logic.tree;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.execution.ProcedureExecutor;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class CommandTree implements Iterable<IProcedure> {

    private INetworkController controller;
    private IProcedure root;

    public void execute() {
        new ProcedureExecutor(controller, controller.getWorld()).start(root);
    }

    public Set<IProcedure> dfsCollect() {
        Set<IProcedure> result = new HashSet<>();
        result.add(root);
        dfs(result, root);
        return result;
    }

    private Set<IProcedure> dfsCollectNoRoot() {
        Set<IProcedure> result = new HashSet<>();
        dfs(result, root);
        return result;
    }

    private void dfs(Set<IProcedure> result, IProcedure node) {
        for (IProcedure next : node.nexts()) {
            if (result.contains(next)) {
                continue;
            }
            result.add(next);
            dfs(result, next);
        }
    }

    @Nonnull
    @Override
    public Iterator<IProcedure> iterator() {
        return dfsCollect().iterator();
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();

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

        IProcedure[] nexts = node.nexts();
        int[] children = new int[nexts.length];
        for (int i = 0, nextsLength = nexts.length; i < nextsLength; i++) {
            int id = idMap.getOrDefault(nexts[i], -1);
            if (id == -1) {
                throw new IllegalArgumentException();
            }
            children[i] = id;
        }

        tag.put("NodeData", node.serialize());
        tag.putIntArray("Children", children);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
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
        return NetworkHelper.retreiveProcedure(nodeNBT.getCompound("NodeData"));
    }

    private void retrieveConnections(List<IProcedure> nodes, IProcedure node, CompoundNBT nodeNBT) {
        int[] children = nodeNBT.getIntArray("Children");
        for (int i = 0; i < children.length; i++) {
            node.nexts()[i] = nodes.get(children[i]);
        }
    }

    public static CommandTree deserializeFrom(CompoundNBT tag) {
        CommandTree tree = new CommandTree();
        tree.deserialize(tag);
        return tree;
    }
}
