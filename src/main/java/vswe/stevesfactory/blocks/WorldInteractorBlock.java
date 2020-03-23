package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

// TODO
public class WorldInteractorBlock extends Block {

    public static final EnumProperty<Direction> AIM_PROPERTY = EnumProperty.create("aim", Direction.class);

    public WorldInteractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }
        Direction aim = player.isCrouching() ? hit.getFace().getOpposite() : hit.getFace();
        world.setBlockState(pos, state.with(AIM_PROPERTY, aim));
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();
        return getDefaultState()
                .with(BlockStateProperties.FACING, direction)
                .with(AIM_PROPERTY, direction);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, AIM_PROPERTY);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WorldInteractorTileEntity();
    }
}
