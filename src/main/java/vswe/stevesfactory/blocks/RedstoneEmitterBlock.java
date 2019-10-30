package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.CapabilityRedstone;
import vswe.stevesfactory.api.capability.IRedstoneHandler;

public class RedstoneEmitterBlock extends BaseBlock {

    public static final EnumProperty<IRedstoneHandler.Type> TYPE_PROPERTY = EnumProperty.create("type", IRedstoneHandler.Type.class);

    public RedstoneEmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container) {
        // TODO each face has a separate type
        container.add(TYPE_PROPERTY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
        return getPowerInternal(world, pos, side, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(BlockState blockState, IBlockReader world, BlockPos pos, Direction side) {
        return getPowerInternal(world, pos, side, true);
    }

    private int getPowerInternal(IBlockReader world, BlockPos pos, Direction side, boolean forceStrong) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof RedstoneEmitterTileEntity) {
            RedstoneEmitterTileEntity emitter = (RedstoneEmitterTileEntity) tile;
            LazyOptional<IRedstoneHandler> cap = emitter.getCapability(CapabilityRedstone.REDSTONE_CAPABILITY, side);
            if (cap.isPresent()) {
                IRedstoneHandler redstone = cap.orElseThrow(RuntimeException::new);
                return forceStrong && redstone.isWeak() ? 0 : redstone.getSignal();
            }
        }
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RedstoneEmitterTileEntity();
    }
}
