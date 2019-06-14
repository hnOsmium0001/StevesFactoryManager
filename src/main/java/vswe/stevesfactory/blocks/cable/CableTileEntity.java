package vswe.stevesfactory.blocks.cable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.api.network.LinkingStatus;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.IConnectable.LinkType;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.CapabilityHelper;
import vswe.stevesfactory.utils.ConnectionHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CableTileEntity extends TileEntity implements ICable {

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
    public LinkingStatus getLinkingStatus() {
        return linkingStatus;
    }

    @Override
    public void updateLinks() {
        ConnectionHelper.updateLinkType(world, linkingStatus);

        connectedNeighbors.clear();
        Iterator<Pair<Direction, LinkType>> it = linkingStatus.connections(LinkType.DEFAULT);
        while (it.hasNext()) {
            Pair<Direction, LinkType> current = it.next();
            BlockPos pos = this.pos.offset(current.getLeft());
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
    }

    @Override
    public void onLeaveNetwork(INetworkController network) {
        connectedNeighbors.forEach(network::removeLink);
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
