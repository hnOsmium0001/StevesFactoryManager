package vswe.stevesfactory.api.logic;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.collections.CompositeUnmodifiableList;

import java.util.*;

public final class ProcedureGraph {

    public static ProcedureGraph create() {
        return new ProcedureGraph();
    }

    private List<ITrigger> triggers = new ArrayList<>();
    private List<IProcedure> regulars = new ArrayList<>();
    private List<IProcedure> all;

    private ProcedureGraph() {
        // Safe downwards erasure cast
        @SuppressWarnings("unchecked") List<IProcedure> triggers = (List<IProcedure>) (List<? extends IProcedure>) this.triggers;
        List<IProcedure> regulars = this.regulars;
        this.all = CompositeUnmodifiableList.of(triggers, regulars);
    }

    public void tick(INetworkController controller) {
        for (ITrigger proc : triggers) {
            proc.tick(controller);
        }
    }

    public void addProcedure(IProcedure procedure) {
        if (procedure instanceof ITrigger) {
            triggers.add((ITrigger) procedure);
        } else {
            regulars.add(procedure);
        }
    }

    public void addProcedure(ITrigger trigger) {
        triggers.add(trigger);
    }

    public List<ITrigger> getTriggers() {
        return triggers;
    }

    public List<IProcedure> getRegulars() {
        return regulars;
    }

    public List<IProcedure> getAllProcedures() {
        return all;
    }

    public Iterator<ITrigger> iteratorValidTriggers() {
        return createValidOnlyIter(triggers);
    }

    public Iterator<IProcedure> iteratorValidRegulars() {
        return createValidOnlyIter(regulars);
    }

    public Iterator<IProcedure> iteratorValidAll() {
        return createValidOnlyIter(all);
    }

    public Iterable<ITrigger> iterableValidTriggers() {
        return () -> createValidOnlyIter(triggers);
    }

    public Iterable<IProcedure> iterableValidRegulars() {
        return () -> createValidOnlyIter(regulars);
    }

    public Iterable<IProcedure> iterableValidAll() {
        return () -> createValidOnlyIter(all);
    }

    private static <T extends IProcedure> Iterator<T> createValidOnlyIter(List<T> list) {
        return new Iterator<T>() {
            private Iterator<T> backing = list.iterator();

            @Override
            public boolean hasNext() {
                return backing.hasNext();
            }

            @Override
            public T next() {
                T t;
                do {
                    t = backing.next();
                } while (!t.isValid());
                return t;
            }
        };
    }

    public void invalidateContent() {
        for (IProcedure procedure : all) {
            procedure.invalidate();
        }
    }

    public void cleanInvalidated() {
        triggers.removeIf(p -> !p.isValid());
        regulars.removeIf(p -> !p.isValid());
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();

        Object2IntMap<IProcedure> idMap = createIDMap();
        ListNBT nodesNBT = new ListNBT();
        for (Object2IntMap.Entry<IProcedure> entry : idMap.object2IntEntrySet()) {
            IProcedure node = entry.getKey();
            int id = entry.getIntValue();

            CompoundNBT nbt = new CompoundNBT();
            nbt.put("Data", node.serialize());
            nbt.putInt("ID", id);
            nodesNBT.add(nbt);
        }
        tag.put("Nodes", nodesNBT);

        ListNBT connectionsNBT = new ListNBT();
        Set<Connection> visited = new HashSet<>();
        for (IProcedure node : idMap.keySet()) {
            for (Connection successor : node.successors()) {
                if (successor != null && !visited.contains(successor)) {
                    visited.add(successor);

                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putInt("FromID", idMap.getInt(successor.getSource()));
                    nbt.putInt("FromIdx", successor.getSourceOutputIndex());
                    nbt.putInt("ToID", idMap.getInt(successor.getDestination()));
                    nbt.putInt("ToIdx", successor.getDestinationInputIndex());
                    connectionsNBT.add(nbt);
                }
            }
            // No need to iterate predecessors because all connections are referenced twice in both successors and predecessors
        }
        tag.put("Connections", connectionsNBT);

        return tag;
    }

    private Object2IntMap<IProcedure> createIDMap() {
        Object2IntMap<IProcedure> idMap = new Object2IntOpenHashMap<>(all.size());
        for (IProcedure node : all) {
            if (!node.isValid()) {
                continue;
            }
            idMap.put(node, idMap.size());
        }
        return idMap;
    }

    public void deserialize(CompoundNBT tag) {
        // Invalidate the procedures to that any allocated resources could be released
        invalidateContent();
        triggers.clear();
        regulars.clear();

        // Deserialize procedures from NBT
        ListNBT nodesNBT = tag.getList("Nodes", Constants.NBT.TAG_COMPOUND);
        Int2ObjectMap<IProcedure> nodes = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < nodesNBT.size(); i++) {
            CompoundNBT nodeNBT = nodesNBT.getCompound(i);
            int id = nodeNBT.getInt("ID");
            IProcedure node = deserializeNode(nodeNBT.getCompound("Data"));
            nodes.put(id, node);
        }

        // Deserialize procedure connections from NBT
        ListNBT connectionNBT = tag.getList("Connections", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < connectionNBT.size(); i++) {
            CompoundNBT nbt = connectionNBT.getCompound(i);
            IProcedure from = nodes.get(nbt.getInt("FromID"));
            int fromIdx = nbt.getInt("FromIdx");
            IProcedure to = nodes.get(nbt.getInt("ToID"));
            int toIdx = nbt.getInt("ToIdx");
            Connection.create(from, fromIdx, to, toIdx);
        }

        // Place deserialized procedures into data structure
        for (int i = 0; i < nodes.size(); i++) {
            addProcedure(nodes.get(i));
        }
    }

    private IProcedure deserializeNode(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));

        IProcedureType<?> p = StevesFactoryManagerAPI.getProceduresRegistry().getValue(id);
        // Not using checkNotNull here because technically the above method returns null is a registry (game state) problem
        Preconditions.checkArgument(p != null, "Unable to find a procedure registered as " + id + "!");

        return p.retrieveInstance(tag);
    }
}
