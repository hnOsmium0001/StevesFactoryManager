package vswe.stevesfactory.blocks.manager;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.blocks.BaseTileEntity;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.ConnectionHelper;
import vswe.stevesfactory.utils.VectorHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FactoryManagerTileEntity extends BaseTileEntity implements ITickableTileEntity, INetworkController, ICable {

    private static final String KEY_CONNECTED_CABLES = "ConnectedCables";
    private static final String KEY_LINKED_INVENTORIES = "LinkedInventories";
    private static final String KEY_LINKING_STATUS = "LinkingStatus";

    private Set<BlockPos> connectedCables = new HashSet<>();
    private Multiset<BlockPos> linkedInventories = HashMultiset.create();

    private LinkingStatus linkingStatus;
    private Set<BlockPos> neighborInventories = new ObjectArraySet<>(6);

    public FactoryManagerTileEntity() {
        super(ModBlocks.factoryManagerTileEntity);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        linkingStatus = new LinkingStatus(pos);
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
        if (!world.isRemote) {
            // TODO logic
        }
    }

    public void openGUI(PlayerEntity player) {
        StevesFactoryManager.logger.debug("Player {} tried to open the GUI of a factory manager at {}", player, pos);
        search();
        // TODO gui
    }

    private void search() {
        StevesFactoryManager.logger.debug("Triggered searching process on a factory manager at {}", pos);

        removeAllCableFromNetwork();

        // Relocate all the cables to prevent the last cable, for example, from being recognized as connected
        // [manager] - [cable] - [removed cable (air)] - [unconnected cable]
        connectedCables.clear();

        addCableToNetwork(this, pos);
        search(pos);
    }

    private void search(BlockPos center) {
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
        StevesFactoryManager.logger.trace("Started removing all cables from the network {}", pos);
        for (BlockPos pos : connectedCables) {
            ICable cable = Objects.requireNonNull((ICable) world.getTileEntity(pos));
            cable.onLeaveNetwork(this);
        }
    }

    public void dump() {
        Logger logger = StevesFactoryManager.logger;
        logger.debug("======== Dumping Factory Manager at {} ========", pos);

        logger.debug("Connected cables:");
        for (BlockPos pos : connectedCables) {
            logger.debug("    {}: {}", pos, world.getTileEntity(pos));
        }

        logger.debug("Linked inventories:");
        for (BlockPos pos : linkedInventories) {
            logger.debug("    {}: {}", pos, world.getTileEntity(pos));
        }

        logger.debug("======== Finished umping Factory Manager ========");
    }

    @Override
    public Set<BlockPos> getConnectedCables() {
        return connectedCables;
    }

    @Override
    public void removeCable(BlockPos cable) {
        connectedCables.remove(cable);
        ((ICable) world.getTileEntity(cable)).onLeaveNetwork(this);
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
        ConnectionHelper.updateLinkType(world, linkingStatus);

//        for (Direction direction : VectorHelper.DIRECTIONS) {
//            BlockPos pos1 = this.pos.offset(direction);
//            // Remove both correct and incorrect links, and add the correct ones back
//            network.removeLink(pos1);
//
//            TileEntity tile = world.getTileEntity(pos1);
//            if (tile != null) {
//                if (ConnectionHelper.shouldLink(tile)) {
//                    network.addLink(pos1);
//                }
//            }
//        }
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
    public void read(CompoundNBT compound) {
        StevesFactoryManager.logger.trace("Restoring data from NBT {}", compound);

        super.read(compound);

        ListNBT serializedCables = compound.getList(KEY_CONNECTED_CABLES, Constants.NBT.TAG_COMPOUND);
        connectedCables.clear();
        for (int i = 0; i < serializedCables.size(); i++) {
            connectedCables.add(NBTUtil.readBlockPos(serializedCables.getCompound(i)));
        }

        ListNBT serializedInventories = compound.getList(KEY_LINKED_INVENTORIES, Constants.NBT.TAG_COMPOUND);
        linkedInventories.clear();
        for (int i = 0; i < serializedInventories.size(); i++) {
            linkedInventories.add(NBTUtil.readBlockPos(serializedInventories.getCompound(i)));
        }

        linkingStatus = LinkingStatus.readFrom(compound.getCompound(KEY_LINKING_STATUS));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        StevesFactoryManager.logger.trace("Writing data into NBT ({})", pos);

        ListNBT serializedCables = new ListNBT();
        for (BlockPos pos : connectedCables) {
            serializedCables.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put(KEY_CONNECTED_CABLES, serializedCables);

        ListNBT serializedInventories = new ListNBT();
        for (BlockPos pos : linkedInventories) {
            serializedInventories.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put(KEY_LINKED_INVENTORIES, serializedInventories);

        compound.put(KEY_LINKING_STATUS, linkingStatus.write());

        return super.write(compound);
    }

}
