package vswe.stevesfactory.blocks.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Logger;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.ICable;
import vswe.stevesfactory.api.INetwork;
import vswe.stevesfactory.setup.ModBlocks;

import java.util.HashSet;
import java.util.Set;

public class FactoryManagerTileEntity extends TileEntity implements ITickableTileEntity, INetwork, ICable {

    private Set<BlockPos> connectedCables = new HashSet<>();

    public FactoryManagerTileEntity() {
        super(ModBlocks.factoryManagerTileEntity);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            // TODO logic
        }
    }

    @Override
    public Set<BlockPos> getConnectedCables() {
        return connectedCables;
    }

    public void openGUI(PlayerEntity player) {
        connectedCables.clear();
        search(pos);
    }

    private void search(BlockPos center) {
        if (world == null)
            return;

        for (Direction direction : Direction.values()) {
            BlockPos neighbor = center.offset(direction);
            TileEntity tile = world.getTileEntity(neighbor);
            if (tile instanceof ICable && !connectedCables.contains(neighbor)) {
                connectedCables.add(neighbor);
                search(neighbor);
            }
        }
    }

    public void dump() {
        Logger logger = StevesFactoryManager.logger;
        logger.debug("======== Dumping Factory Manager at {} ========", pos.toString());

        logger.debug("Connected cables:");
        for (BlockPos pos : connectedCables) {
            logger.debug("{}: {}", pos, world.getTileEntity(pos));
        }

        logger.debug("======== Finished dumping Factory Manager ========");
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
