package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {

    public BaseBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
//        if (!world.isRemote) {
//            TileEntity tile = world.getTileEntity(pos);
//            if (tile instanceof BaseTileEntity) {
//                ((BaseTileEntity) tile).onRemoved();
//            }
//        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof BaseTileEntity) {
                ((BaseTileEntity) tile).onRemoved();
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

}
