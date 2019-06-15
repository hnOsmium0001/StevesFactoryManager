package vswe.stevesfactory.blocks.cable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CableBlock extends Block {

    public CableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof CableTileEntity) {
            ((CableTileEntity) tile).updateConnections(neighbor);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof CableTileEntity) {
            ((CableTileEntity) tile).onRemoved();
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CableTileEntity();
    }

}
