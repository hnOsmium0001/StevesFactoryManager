package vswe.stevesfactory.blocks.cable;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.*;
import vswe.stevesfactory.blocks.BaseTileEntity;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.IOHelper;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CableTileEntity extends BaseTileEntity implements ICable {

    private static final String KEY_JOINED_NETWORKS = "JoinedNetworks";
    private static final String KEY_LINKING_STATUS = "LinkingStatus";
    private static final String KEY_NEIGHBOR_INVENTORIES = "NeighborInventories";

    private transient List<BlockPos> joinedNetworks = new ArrayList<>();

    private LinkingStatus linkingStatus;
    private Set<BlockPos> neighborInventories = new ObjectArraySet<>(6);

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        linkingStatus = new LinkingStatus(pos);
    }

    @Override
    public void onRemoved() {
        for (INetworkController network : getJoinedNetworkControllers()) {
            // This will fire onLeaveNetwork, so we don't have to remove links here
            network.removeCable(pos);
        }
    }

    @Override
    public void updateLinks() {
        assert world != null;
        StevesFactoryManager.logger.debug("Updating links of CableTileEntity at {}", pos);
        NetworkHelper.updateLinkType(world, linkingStatus);

        List<INetworkController> networks = getJoinedNetworkControllers();
        networks.forEach(network -> neighborInventories.forEach(network::removeLink));
        neighborInventories.clear();
        for (BlockPos neighbor : getNeighbors()) {
            TileEntity tile = world.getTileEntity(neighbor);
            if (NetworkHelper.shouldLink(tile)) {
                neighborInventories.add(neighbor);
            }
        }
        networks.forEach(network -> neighborInventories.forEach(network::addLink));

        StevesFactoryManager.logger.debug("Updated links of the CableTileEntity. Result: {}", neighborInventories);
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
    public void onJoinNetwork(INetworkController network) {
        joinedNetworks.add(network.getPos());
        updateLinks();
        StevesFactoryManager.logger.trace("Cable {} joined network {}", pos, network);
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
        joinedNetworks.remove(network.getPos());
        neighborInventories.forEach(network::removeLink);
        StevesFactoryManager.logger.trace("Cable {} left network {}", pos, network);
    }

    public List<BlockPos> getJoinedNetworks() {
        return joinedNetworks;
    }

    private List<INetworkController> getJoinedNetworkControllers() {
        assert world != null;
        return NetworkHelper.getNetworksAt(world, joinedNetworks);
    }

    @Override
    public boolean isCable() {
        return true;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        linkingStatus = LinkingStatus.readFrom(compound.getCompound(KEY_LINKING_STATUS));
        joinedNetworks = IOHelper.readBlockPoses(compound.getList(KEY_JOINED_NETWORKS, Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        neighborInventories = IOHelper.readBlockPoses(compound.getList(KEY_NEIGHBOR_INVENTORIES, Constants.NBT.TAG_COMPOUND), new HashSet<>());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put(KEY_LINKING_STATUS, linkingStatus.write());
        compound.put(KEY_JOINED_NETWORKS, IOHelper.writeBlockPoses(joinedNetworks));
        compound.put(KEY_NEIGHBOR_INVENTORIES, IOHelper.writeBlockPoses(neighborInventories));

        return super.write(compound);
    }
}
