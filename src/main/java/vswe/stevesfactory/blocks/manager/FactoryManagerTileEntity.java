package vswe.stevesfactory.blocks.manager;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.api.ICable;
import vswe.stevesfactory.api.INetwork;
import vswe.stevesfactory.setup.ModBlocks;

import java.util.HashSet;
import java.util.Set;

public class FactoryManagerTileEntity extends TileEntity implements ITickableTileEntity, INetwork {

    private Set<ICable> connectedCables = new HashSet<>();

    public FactoryManagerTileEntity() {
        super(ModBlocks.factoryManagerTileEntity);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            // Update extremely infrequently
            // Note that it will update every time the player opens the GUI TODO
            if (world.getGameTime() % 6000 == 0) {
                search(pos);
            }
        }
    }

    @Override
    public Set<? extends ICable> getConnectedCables() {
        return connectedCables;
    }

    private void search(BlockPos center) {
        if (world == null)
            return;

        connectedCables.clear();
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            TileEntity tile = world.getTileEntity(neighbor);
            if (tile instanceof ICable && !connectedCables.contains(tile)) {
                ICable element = (ICable) tile;
                connectedCables.add(element);
                search(center);
            }
        }
    }

}
