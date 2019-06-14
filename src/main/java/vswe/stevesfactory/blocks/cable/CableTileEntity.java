package vswe.stevesfactory.blocks.cable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.api.network.Connections;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.IConnectable.ConnectionType;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.ConnectionHelper;

import java.util.Iterator;

public class CableTileEntity extends TileEntity implements ICable {

    private Connections connections;

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

        Iterator<Pair<BlockPos, ConnectionType>> it = connections.connections(ConnectionType.DEFAULT);
        while (it.hasNext()) {
            Pair<BlockPos, ConnectionType> current = it.next();
            TileEntity tile = world.getTileEntity(current.getLeft());
            if (tile != null) {
                // TODO capability detector
            }
        }
    }

    public void updateConnections(BlockPos updatedHint) {
        // TODO use hint
        updateConnections();
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
