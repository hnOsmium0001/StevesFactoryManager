package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.utils.VectorHelper;

public abstract class BaseTileEntity extends TileEntity {

    private Iterable<BlockPos> neighbors;

    public BaseTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        neighbors = VectorHelper.neighbors(pos);
    }

    public void onRemoved() {
    }

    public Iterable<BlockPos> getNeighbors() {
        return neighbors;
    }

}
