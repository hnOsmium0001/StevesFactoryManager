package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import vswe.stevesfactory.utils.Utils;

public class BUDBlock extends Block {

    public BUDBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof BUDTileEntity) {
            ((BUDTileEntity) tile).onNeighborChanged(fromPos);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BUDTileEntity();
    }
}
