package vswe.stevesfactory.blocks.cable;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.blocks.BaseTileEntity;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.ConnectionHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CableTileEntity extends BaseTileEntity implements ICable {

    private static final String KEY_JOINED_NETWORKS = "JoinedNetworks";
    private static final String KEY_LINKING_STATUS = "LinkingStatus";
    private static final String KEY_NEIGHBOR_INVENTORIES = "NeighborInventories";

    private transient List<INetworkController> joinedNetworks = new ArrayList<>();

    private LinkingStatus linkingStatus;
    private Set<BlockPos> linkedNeighbors = new ObjectArraySet<>(6);

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
        for (INetworkController network : joinedNetworks) {
            // This will fire onLeaveNetwork, so we don't have to remove links here
            network.removeCable(pos);
        }
    }

    @Override
    public void updateLinks() {
        StevesFactoryManager.logger.debug("Updating links of CableTileEntity at {}", pos);
        ConnectionHelper.updateLinkType(world, linkingStatus);

        joinedNetworks.forEach(network -> linkedNeighbors.forEach(network::removeLink));
        linkedNeighbors.clear();
        for (BlockPos neighbor : getNeighbors()) {
            TileEntity tile = world.getTileEntity(neighbor);
            if (ConnectionHelper.shouldLink(tile)) {
                linkedNeighbors.add(neighbor);
            }
        }
        joinedNetworks.forEach(network -> linkedNeighbors.forEach(network::addLink));

        StevesFactoryManager.logger.debug("Updated links of the CableTileEntity. Result: {}", linkedNeighbors);
    }

    @Override
    public LinkingStatus getLinkingStatus() {
        return linkingStatus;
    }

    @Nullable
    @Override
    public Set<BlockPos> getNeighborInventories() {
        return linkedNeighbors;
    }

    @Override
    public void onJoinNetwork(INetworkController network) {
        joinedNetworks.add(network);
        updateLinks();
        StevesFactoryManager.logger.trace("Cable {} joined network {}", pos, network);
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
        joinedNetworks.remove(network);
        linkedNeighbors.forEach(network::removeLink);
        StevesFactoryManager.logger.trace("Cable {} left network {}", pos, network);
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

        linkedNeighbors.clear();
        ListNBT serializedNeighbors = compound.getList(KEY_NEIGHBOR_INVENTORIES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < serializedNeighbors.size(); i++) {
            linkedNeighbors.add(NBTUtil.readBlockPos(serializedNeighbors.getCompound(i)));
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
        for (BlockPos pos : linkedNeighbors) {
            serializedNeighbors.add(NBTUtil.writeBlockPos(pos));
        }
        compound.put(KEY_NEIGHBOR_INVENTORIES, serializedNeighbors);

        return super.write(compound);
    }

}
