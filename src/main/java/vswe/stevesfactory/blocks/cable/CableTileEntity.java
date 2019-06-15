package vswe.stevesfactory.blocks.cable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.CapabilityHelper;
import vswe.stevesfactory.utils.ConnectionHelper;
import vswe.stevesfactory.utils.VectorHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CableTileEntity extends TileEntity implements ICable {

    private static final String KEY_LINKING_STATUS = "LinkingStatus";

    private transient List<INetworkController> joinedNetworks = new ArrayList<>();

    private LinkingStatus linkingStatus;
    private transient List<BlockPos> connectedNeighbors = new ArrayList<>(6);

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public void onLoad() {
        linkingStatus = new LinkingStatus(pos);
        updateLinks();
    }

    public void onRemoved() {
        for (INetworkController network : joinedNetworks) {
            network.getConnectedCables().remove(pos);
            connectedNeighbors.forEach(network::removeLink);
        }
    }

    @Override
    public LinkingStatus getLinkingStatus() {
        return linkingStatus;
    }

    @Override
    public void updateLinks() {
        ConnectionHelper.updateLinkType(world, linkingStatus);

        connectedNeighbors.clear();
        Iterator<BlockPos> it = VectorHelper.neighborsIterator(linkingStatus.getCenter());
        while (it.hasNext()) {
            BlockPos pos = it.next();
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && !(tile instanceof ICable)) {
                if (CapabilityHelper.hasCapabilityAtAll(tile, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                        CapabilityHelper.hasCapabilityAtAll(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
                    connectedNeighbors.add(pos);
                }
            }
        }
    }

    public void updateConnections(BlockPos updatedHint) {
        // TODO use hint
        updateLinks();
    }

    @Override
    public void onJoinNetwork(INetworkController network) {
        connectedNeighbors.forEach(network::addLink);
        joinedNetworks.add(network);
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
        connectedNeighbors.forEach(network::removeLink);
        joinedNetworks.remove(network);
    }

    @Override
    public boolean isCable() {
        return true;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        linkingStatus = LinkingStatus.readFrom(compound.getCompound(KEY_LINKING_STATUS));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put(KEY_LINKING_STATUS, linkingStatus.write());

        return super.write(compound);
    }

}
