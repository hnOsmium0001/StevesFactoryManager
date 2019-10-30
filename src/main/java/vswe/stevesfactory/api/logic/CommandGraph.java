package vswe.stevesfactory.api.logic;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.network.INetworkController;

import javax.annotation.Nonnull;
import java.util.*;

public final class CommandGraph implements Iterable<IProcedure> {

    private final INetworkController controller;
    private IProcedure root;

    public CommandGraph(INetworkController controller, IProcedure root) {
        this.controller = controller;
        this.root = root;
    }

    public CommandGraph(INetworkController controller) {
        this.controller = controller;
    }

    public boolean isValid() {
        return controller != null && controller.isValid() && controller.isGraphValid(this) && root != null;
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

    @Nonnull
    @Override
    public Iterator<IProcedure> iterator() {
        return collect().iterator();
    }

    public Set<IProcedure> collect() {
        Preconditions.checkState(isValid());
        Set<IProcedure> result = new HashSet<>();
        result.add(root);
        dfs(result, root);
        return result;
    }

    private Set<IProcedure> dfsCollectNoRoot() {
        Set<IProcedure> result = new HashSet<>();
        if (root != null) {
            dfs(result, root);
        }
        return result;
    }

    private void dfs(Set<IProcedure> result, IProcedure node) {
        for (Connection connection : node.successors()) {
            if (connection == null) {
                continue;
            }
            IProcedure next = connection.getDestination();
            if (result.contains(next)) {
                continue;
            }
            result.add(next);
            dfs(result, next);
        }
    }

    public CommandGraph inducedSubgraph(IProcedure node) {
        Preconditions.checkState(isValid());
        return new CommandGraph(controller, node);
    }

    public CompoundNBT serialize() {
        Preconditions.checkState(isValid());
        CompoundNBT tag = new CompoundNBT();

        tag.putString("Dimension", Objects.requireNonNull(controller.getDimension().getRegistryName()).toString());
        tag.put("ControllerPos", NBTUtil.writeBlockPos(controller.getPosition()));

        Object2IntMap<IProcedure> idMap = createIDMap();
        ListNBT nodesNBT = new ListNBT();
        for (Object2IntMap.Entry<IProcedure> entry : idMap.object2IntEntrySet()) {
            IProcedure node = entry.getKey();
            int id = entry.getIntValue();
            nodesNBT.add(serializeNode(node, id));
        }

        ListNBT connectionsNBT = new ListNBT();
        Set<Connection> visited = new HashSet<>();
        for (IProcedure node : idMap.keySet()) {
            for (Connection successor : node.successors()) {
                if (successor != null && !visited.contains(successor)) {
                    visited.add(successor);

                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putInt("From", idMap.getInt(successor.getSource()));
                    nbt.putInt("FromOut", successor.getSourceOutputIndex());
                    nbt.putInt("To", idMap.getInt(successor.getDestination()));
                    nbt.putInt("ToIn", successor.getDestinationInputIndex());
                    connectionsNBT.add(nbt);
                }
            }
            // No need to iterate predecessors because all connections are referenced twice in both successors and predecessors
        }

        // This is for compatibility reasons
        // If we ever need to make root not the first element, this might become useful
        tag.putInt("RootID", 0);
        tag.put("Nodes", nodesNBT);
        tag.put("Connections", connectionsNBT);

        return tag;
    }

    private Object2IntMap<IProcedure> createIDMap() {
        Set<IProcedure> nodes = dfsCollectNoRoot();
        Object2IntMap<IProcedure> idMap = new Object2IntOpenHashMap<>(nodes.size() + 1);
        idMap.put(root, 0);
        for (IProcedure node : nodes) {
            idMap.put(node, idMap.size());
        }
        return idMap;
    }

    private CompoundNBT serializeNode(IProcedure node, int id) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("NodeData", node.serialize());
        tag.putInt("ID", id);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        ListNBT nodesNBT = tag.getList("Nodes", Constants.NBT.TAG_COMPOUND);
        Int2ObjectMap<IProcedure> nodes = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < nodesNBT.size(); i++) {
            CompoundNBT nodeNBT = nodesNBT.getCompound(i);
            int id = nodeNBT.getInt("ID");
            IProcedure node = deserializeNode(nodeNBT.getCompound("NodeData"));
            nodes.put(id, node);
        }

        ListNBT connectionNBT = tag.getList("Connections", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < connectionNBT.size(); i++) {
            CompoundNBT nbt = connectionNBT.getCompound(i);
            IProcedure from = nodes.get(nbt.getInt("From"));
            int fromOut = nbt.getInt("FromOut");
            IProcedure to = nodes.get(nbt.getInt("To"));
            int toIn = nbt.getInt("ToIn");
            Connection.create(from, fromOut, to, toIn);
        }

        int rootID = tag.getInt("RootID");
        this.root = nodes.get(rootID);
    }

    private IProcedure deserializeNode(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));

        IProcedureType<?> p = StevesFactoryManagerAPI.getProceduresRegistry().getValue(id);
        // Not using checkNotNull here because technically the above method returns null is a registry (game state) problem
        Preconditions.checkArgument(p != null, "Unable to find a procedure registered as " + id + "!");

        IProcedure procedure = p.retrieveInstance(tag);
        procedure.setGraph(this);
        return procedure;
    }

    /**
     * Deserialize the whole command graph from the given compound.
     *
     * @param tag        Serialized data produced by {@link #deserialize(CompoundNBT)}
     * @param controller Target controller object to reference to
     * @return A valid command graph that should produce the same compound if {@link #serialize()} is called.
     */
    public static CommandGraph deserializeFrom(CompoundNBT tag, INetworkController controller) {
        CommandGraph graph = new CommandGraph(controller);
        graph.deserialize(tag);
        return graph;
    }
}
