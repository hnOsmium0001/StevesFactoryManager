package vswe.stevesfactory.blocks.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.IConnectable.LinkType;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.CapabilityHelper;
import vswe.stevesfactory.utils.ConnectionHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FactoryManagerTileEntity extends TileEntity implements ITickableTileEntity, INetworkController, ICable {

    private Set<BlockPos> connectedCables = new HashSet<>();
    private Set<BlockPos> linkedInventories = new HashSet<>();
    private LinkingStatus linkingStatus;

    public FactoryManagerTileEntity() {
        super(ModBlocks.factoryManagerTileEntity);
    }

    @Override
    public void onLoad() {
        linkingStatus = new LinkingStatus(pos);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            // TODO logic
        }
    }

    public void openGUI(PlayerEntity player) {
        search();
    }

    private void search() {
        notifyLeavedCables();
        // Relocate all the cables to prevent the last cable, for example, from being recognized as connected
        // [manager] - [cable] - [removed cable (air)] - [unconnected cable]
        connectedCables.clear();

        // Update the manager itself as a cable
        connectedCables.add(pos);
        updateLinks();

        search(pos);
    }

    private void notifyLeavedCables() {
        for (BlockPos pos : connectedCables) {
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof ICable)) {
                connectedCables.remove(pos);
            }
        }
    }

    private void search(BlockPos center) {
        if (world == null)
            return;

        for (Direction direction : Direction.values()) {
            BlockPos neighbor = center.offset(direction);
            TileEntity tile = world.getTileEntity(neighbor);
            if (tile instanceof ICable && !connectedCables.contains(neighbor)) {
                ICable cable = (ICable) tile;
                cable.updateLinks();
                cable.onJoinNetwork(this);
                connectedCables.add(neighbor);
                search(neighbor);
            }
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

        logger.debug("======== Finished dumping Factory Manager ========");
    }

    @Override
    public Set<BlockPos> getConnectedCables() {
        return connectedCables;
    }

    @Override
    public Set<BlockPos> getLinkedInventories() {
        return linkedInventories;
    }

    @Override
    public boolean addLink(BlockPos pos) {
        return linkedInventories.add(pos);
    }

    @Override
    public boolean removeLink(BlockPos pos) {
        return linkedInventories.remove(pos);
    }

    @Override
    public LinkingStatus getLinkingStatus() {
        return linkingStatus;
    }

    @Override
    public void updateLinks() {
        updateLinksInternal(this);
    }

    private void updateLinksInternal(INetworkController network) {
        ConnectionHelper.updateLinkType(world, linkingStatus);

        Iterator<Pair<Direction, LinkType>> it = linkingStatus.connections(LinkType.DEFAULT);
        while (it.hasNext()) {
            Pair<Direction, LinkType> current = it.next();
            BlockPos pos = this.pos.offset(current.getLeft());
            // Remove both correct and incorrect links, and add the correct ones back
            network.removeLink(pos);

            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && !(tile instanceof ICable)) {
                if (CapabilityHelper.shouldLink(tile)) {
                    network.addLink(pos);
                }
            }
        }
    }

    @Override
    public void onJoinNetwork(INetworkController network) {
        if (network != this) {
            updateLinksInternal(network);
        }
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
