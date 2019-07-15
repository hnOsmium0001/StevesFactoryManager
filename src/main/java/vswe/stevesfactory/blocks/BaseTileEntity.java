package vswe.stevesfactory.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.utils.VectorHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
