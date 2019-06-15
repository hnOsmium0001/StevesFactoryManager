package vswe.stevesfactory.blocks.cable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.blocks.BaseTileEntity;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.ConnectionHelper;
import vswe.stevesfactory.utils.VectorHelper;

import java.util.ArrayList;
import java.util.List;

public class CableTileEntity extends BaseTileEntity implements ICable {

    public static final String KEY_JOINED_NETWORKS = "JoinedNetworks";
    private static final String KEY_LINKING_STATUS = "LinkingStatus";
    private static final String KEY_NEIGHBOR_INVENTORIES = "NeighborInventories";

    private transient List<INetworkController> joinedNetworks = new ArrayList<>();

    private LinkingStatus linkingStatus;
    private List<BlockPos> connectedNeighbors = new ArrayList<>(6);

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public void onLoad() {
        linkingStatus = new LinkingStatus(pos);
    }

    @Override
    public void onRemoved() {
        for (INetworkController network : joinedNetworks) {
            // This will fire onLeaveNetwork, so we don't have to remove links here
            network.removeCable(pos);
        }
    }

    @Override
    public LinkingStatus getLinkingStatus() {
        return linkingStatus;
    }

    @Override
    public void updateLinks() {
        StevesFactoryManager.logger.trace("Updating links of CableTileEntity at {}", pos);
        ConnectionHelper.updateLinkType(world, linkingStatus);

        joinedNetworks.forEach(network -> connectedNeighbors.forEach(network::removeLink));
        connectedNeighbors.clear();
        for (Direction direction : VectorHelper.DIRECTIONS) {
            BlockPos pos = this.pos.offset(direction);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                if (ConnectionHelper.shouldLink(tile)) {
                    connectedNeighbors.add(pos);
                }
            }
        }
        joinedNetworks.forEach(network -> connectedNeighbors.forEach(network::addLink));
        StevesFactoryManager.logger.trace("Updated links of the aCableTileEntity");
    }

    public void updateLinks(BlockPos updatedHint) {
        // TODO use hint
        updateLinks();
    }

    @Override
    public void onJoinNetwork(INetworkController network) {
        joinedNetworks.add(network);
        updateLinks();
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
        joinedNetworks.remove(network);
        connectedNeighbors.forEach(network::removeLink);
    }

    @Override
    public boolean isCable() {
        return true;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        joinedNetworks.clear();
        ListNBT serializedNetworkControllers = compound.getList(KEY_JOINED_NETWORKS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < serializedNetworkControllers.size(); i++) {
            BlockPos pos = NBTUtil.readBlockPos(serializedNetworkControllers.getCompound(i));
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof INetworkController) {
                joinedNetworks.add((INetworkController) tile);
            } else {
                StevesFactoryManager.logger.warn("Expected network controller on {} but found {}", pos, tile);
            }
        }

        linkingStatus = LinkingStatus.readFrom(compound.getCompound(KEY_LINKING_STATUS));

        connectedNeighbors.clear();
        ListNBT serializedNeighbors = compound.getList(KEY_NEIGHBOR_INVENTORIES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < serializedNeighbors.size(); i++) {
            connectedNeighbors.add(NBTUtil.readBlockPos(serializedNeighbors.getCompound(i)));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put(KEY_LINKING_STATUS, linkingStatus.write());

        ListNBT serializedNetworkControllers = new ListNBT();
        for (INetworkController network : joinedNetworks) {
            serializedNetworkControllers.add(NBTUtil.writeBlockPos(network.getPos()));
        }
        compound.put(KEY_JOINED_NETWORKS, serializedNetworkControllers);

        ListNBT serializedNeighbors = new ListNBT();
        for (BlockPos pos : connectedNeighbors) {
            serializedNeighbors.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put(KEY_NEIGHBOR_INVENTORIES, serializedNeighbors);

        return super.write(compound);
    }

}
