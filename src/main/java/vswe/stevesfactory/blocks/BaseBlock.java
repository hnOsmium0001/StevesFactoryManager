package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {

    public BaseBlock(Properties properties) {
        super(properties);
    }


    /**
     * Don't override this method for the purpose of getting triggers on block removal. Instead, override {@link #onBlockHarvested(World,
     * BlockPos, BlockState, PlayerEntity)}.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
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

}
