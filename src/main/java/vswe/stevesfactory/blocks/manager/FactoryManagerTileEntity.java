package vswe.stevesfactory.blocks.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.Connections;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetwork;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.ConnectionHelper;

import java.util.HashSet;
import java.util.Set;

public class FactoryManagerTileEntity extends TileEntity implements ITickableTileEntity, INetwork, ICable {

    private Set<BlockPos> connectedCables = new HashSet<>();
    private Set<BlockPos> connectedInventories = new HashSet<>();
    private Connections connections;

    public FactoryManagerTileEntity() {
        super(ModBlocks.factoryManagerTileEntity);
    }

    @Override
    public void onLoad() {
        connections = new Connections(pos);
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
        updateConnections();

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
                cable.updateConnections();
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

        logger.debug("Connected inventories:");
        for (BlockPos pos : connectedInventories) {
            logger.debug("    {}: {}", pos, world.getTileEntity(pos));
        }

        logger.debug("======== Finished dumping Factory Manager ========");
    }

    @Override
    public Set<BlockPos> getConnectedCables() {
        return connectedCables;
    }

    @Override
    public boolean addConnectedInventory(BlockPos pos) {
        return connectedInventories.add(pos);
    }

    @Override
    public boolean removeConnectedInventory(BlockPos pos) {
        return connectedInventories.remove(pos);
    }

    @Override
    public boolean isCable() {
        return true;
    }

    @Override
    public Connections getConnectionStatus() {
        return connections;
    }

    @Override
    public void updateConnections() {
        ConnectionHelper.updateConnectionType(world, connections);
    }

    @Override
    public void onJoinNetwork(INetwork network) {
    }

    @Override
    public void onLeaveNetwork(INetwork network) {
    }

}
