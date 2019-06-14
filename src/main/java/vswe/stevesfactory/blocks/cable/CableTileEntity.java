package vswe.stevesfactory.blocks.cable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import vswe.stevesfactory.api.network.Connections;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.IConnectable.ConnectionType;
import vswe.stevesfactory.api.network.INetwork;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.CapabilityHelper;
import vswe.stevesfactory.utils.ConnectionHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CableTileEntity extends TileEntity implements ICable {

    private Connections connections;
    private List<BlockPos> connectedNeighbors = new ArrayList<>(6);

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public void onLoad() {
        connections = new Connections(pos);
    }

    @Override
    public Connections getConnectionStatus() {
        return connections;
    }

    @Override
    public void updateConnections() {
        ConnectionHelper.updateConnectionType(world, connections);

        connectedNeighbors.clear();
        Iterator<Pair<Direction, ConnectionType>> it = connections.connections(ConnectionType.DEFAULT);
        while (it.hasNext()) {
            Pair<Direction, ConnectionType> current = it.next();
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
        updateConnections();
    }

    @Override
    public void onJoinNetwork(INetwork network) {
        connectedNeighbors.forEach(network::addConnectedInventory);
    }

    @Override
    public void onLeaveNetwork(INetwork network) {
        connectedNeighbors.forEach(network::removeConnectedInventory);
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
