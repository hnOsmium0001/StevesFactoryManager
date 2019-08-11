package vswe.stevesfactory.blocks.manager;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.*;
import vswe.stevesfactory.blocks.BaseTileEntity;
import vswe.stevesfactory.logic.execution.ITickable;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.network.NetworkHandler;
import vswe.stevesfactory.network.PacketSyncCommandGraphs;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FactoryManagerTileEntity extends BaseTileEntity implements ITickableTileEntity, INetworkController, ICable {

    private static final String KEY_CONNECTED_CABLES = "ConnectedCables";
    private static final String KEY_LINKED_INVENTORIES = "LinkedInventories";
    private static final String KEY_LINKING_STATUS = "LinkingStatus";
    private static final String KEY_COMMAND_GRAPHS = "CommandGraphs";

    private Set<BlockPos> connectedCables = new HashSet<>();
    private Multiset<BlockPos> linkedInventories = HashMultiset.create();

    private LinkingStatus linkingStatus;
    private Set<BlockPos> neighborInventories = new ObjectArraySet<>(6);

    private Set<CommandGraph> graphs = new HashSet<>();

    public FactoryManagerTileEntity() {
        super(ModBlocks.factoryManagerTileEntity);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        linkingStatus = new LinkingStatus(pos);

        assert world != null;
        if (!world.isRemote) {
            if (!connectedCables.contains(pos)) {
                addCableToNetwork(this, pos);
            }
        }
    }

    @Override
    public void onChunkUnloaded() {
        removeAllCableFromNetwork();
    }

    @Override
    public void onRemoved() {
        removeAllCableFromNetwork();
    }

    @Override
    public void tick() {
        assert world != null;
        if (!world.isRemote) {
            for (CommandGraph graph : graphs) {
                IProcedure hat = graph.getRoot();
                if (hat instanceof ITickable) {
                    ((ITickable) hat).tick();
                }
            }
        }
    }

    public void activate(PlayerEntity player) {
        StevesFactoryManager.logger.trace("Player {} activated a factory manager at {}", player, pos);
        NetworkHandler.sendTo((ServerPlayerEntity) player, new PacketSyncCommandGraphs(graphs, getDimension(), getPos()));
        search();
    }

    private void search() {
        StevesFactoryManager.logger.trace("Triggered searching process on a factory manager at {}", pos);

        removeAllCableFromNetwork();

        // Relocate all the cables to prevent the last cable, for example, from being recognized as connected
        // [manager] - [cable] - [removed cable (air)] - [unconnected cable]
        connectedCables.clear();

        addCableToNetwork(this, pos);
        search(pos);
    }

    private void search(BlockPos center) {
        assert world != null;
        StevesFactoryManager.logger.trace("Searching at cable {}", center);
        for (Direction direction : VectorHelper.DIRECTIONS) {
            BlockPos neighbor = center.offset(direction);
            TileEntity tile = world.getTileEntity(neighbor);
            if (tile instanceof ICable && !connectedCables.contains(neighbor)) {
                addCableToNetwork((ICable) tile, neighbor);
                // Recursive search (DFS)
                search(neighbor);
            }
        }
    }

    private void addCableToNetwork(ICable cable, BlockPos pos) {
        cable.onJoinNetwork(this);
        connectedCables.add(pos);
    }

    private void removeAllCableFromNetwork() {
        assert world != null;
        StevesFactoryManager.logger.trace("Started removing all cables from the network {}", pos);
        for (BlockPos pos : connectedCables) {
            @Nullable
            ICable cable = (ICable) world.getTileEntity(pos);
            if (cable != null) {
                cable.onLeaveNetwork(this);
            }
        }
    }

    public void dump() {
        assert world != null;
        Logger logger = StevesFactoryManager.logger;
        logger.debug("======== Dumping Factory Manager at {} ========", pos);

        logger.debug("Connected cables:");
        for (BlockPos pos : connectedCables) {
            if (this.pos.equals(pos)) {
                logger.debug("    This controller: {}", this);
            } else {
                logger.debug("    {}: {}", pos, world.getTileEntity(pos));
            }
        }

        logger.debug("Linked inventories:");
        for (BlockPos pos : linkedInventories) {
            logger.debug("    {}: {}", pos, world.getTileEntity(pos));
        }

        logger.debug("======== Finished dumping Factory Manager ========");
    }

    @Override
    public DimensionType getDimension() {
        assert world != null;
        return world.getDimension().getType();
    }

    @Override
    public Set<BlockPos> getConnectedCables() {
        return connectedCables;
    }

    @Override
    public void removeCable(BlockPos cable) {
        assert world != null;
        connectedCables.remove(cable);
        Objects.requireNonNull((ICable) world.getTileEntity(cable)).onLeaveNetwork(this);
    }

    @Override
    public Set<BlockPos> getLinkedInventories() {
        return linkedInventories.elementSet();
    }

    /**
     * @return {@code true} always. See {@link Multiset#add(Object)} for details.
     */
    @Override
    public boolean addLink(BlockPos pos) {
        StevesFactoryManager.logger.trace("Added link");
        return linkedInventories.add(pos);
    }

    @Override
    public boolean addLinks(Collection<BlockPos> poses) {
        StevesFactoryManager.logger.trace("Added {} links", poses.size());
        return linkedInventories.addAll(poses);
    }

    @Override
    public void removeAllLinks() {
        linkedInventories.clear();
    }

    public Set<CommandGraph> getCommandGraphs() {
        return graphs;
    }

    @Override
    public boolean addCommandGraph(CommandGraph graph) {
        return graphs.add(graph);
    }

    @Override
    public boolean addCommandGraphs(Collection<CommandGraph> graphs) {
        return this.graphs.addAll(graphs);
    }

    @Override
    public boolean removeCommandGraph(CommandGraph graph) {
        return graphs.remove(graph);
    }

    @Override
    public void removeAllCommandGraphs() {
        graphs.clear();
    }

    /**
     * @return {@code true} always. See {@link Multiset#add(Object)} for details.
     */
    @Override
    public boolean removeLink(BlockPos pos) {
        StevesFactoryManager.logger.trace("Removed link");
        return linkedInventories.remove(pos);
    }

    @Override
    public LinkingStatus getLinkingStatus() {
        return linkingStatus;
    }

    @Nullable
    @Override
    public Set<BlockPos> getNeighborInventories() {
        return neighborInventories;
    }

    @Override
    public void updateLinks() {
        assert world != null;
        NetworkHelper.updateLinkType(world, linkingStatus);

        // TODO add links
    }

    @Override
    public void onJoinNetwork(INetworkController network) {
        if (network != this) {
            updateLinks();
            neighborInventories.forEach(network::addLink);
        }
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
        if (network != this) {
            neighborInventories.forEach(network::removeLink);
        }
    }

    @Override
    public boolean isCable() {
        return true;
    }

    @Override
    public void sync() {
        assert world != null;
        if (world.isRemote) {
            NetworkHandler.sendToServer(new PacketSyncCommandGraphs(graphs, getDimension(), getPos()));
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        StevesFactoryManager.logger.trace("Restoring data from NBT {}", compound);

        super.read(compound);

        linkingStatus = LinkingStatus.readFrom(compound.getCompound(KEY_LINKING_STATUS));
        connectedCables = IOHelper.readBlockPoses(compound.getList(KEY_CONNECTED_CABLES, Constants.NBT.TAG_COMPOUND), new HashSet<>());

        ListNBT serializedInventories = compound.getList(KEY_LINKED_INVENTORIES, Constants.NBT.TAG_COMPOUND);
        linkedInventories.clear();
        for (int i = 0; i < serializedInventories.size(); i++) {
            linkedInventories.add(NBTUtil.readBlockPos(serializedInventories.getCompound(i)));
        }

        ListNBT commandGraphs = compound.getList(KEY_COMMAND_GRAPHS, Constants.NBT.TAG_COMPOUND);
        graphs.clear();
        for (int i = 0; i < commandGraphs.size(); i++) {
            // TODO cross implementation compat
            graphs.add(CommandGraph.deserializeFrom(commandGraphs.getCompound(i)));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        StevesFactoryManager.logger.trace("Writing data into NBT ({})", pos);

        compound.put(KEY_LINKING_STATUS, linkingStatus.write());
        compound.put(KEY_CONNECTED_CABLES, IOHelper.writeBlockPoses(connectedCables));

        ListNBT serializedInventories = new ListNBT();
        for (BlockPos pos : linkedInventories) {
            serializedInventories.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put(KEY_LINKED_INVENTORIES, serializedInventories);

        ListNBT commandGraphs = new ListNBT();
        for (CommandGraph graph : graphs) {
            commandGraphs.add(graph.serialize());
        }
        compound.put("CommandGraphs", commandGraphs);

        return super.write(compound);
    }
}
