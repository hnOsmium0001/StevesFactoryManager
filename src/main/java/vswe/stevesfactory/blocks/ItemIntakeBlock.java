package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ItemIntakeBlock extends Block {

    private Supplier<TileEntity> tileEntityFactory;

    public ItemIntakeBlock(Supplier<TileEntity> tileEntityFactory, Properties properties) {
        super(properties);
        this.tileEntityFactory = tileEntityFactory;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ItemIntakeTileEntity) {
            ItemIntakeTileEntity intake = (ItemIntakeTileEntity) tile;
            intake.cycleMode();
            player.sendStatusMessage(new TranslationTextComponent("message.sfm.ItemIntake.CycleMode", new TranslationTextComponent(intake.getMode().nameKey)), true);
            return true;
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState()
                .with(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileEntityFactory.get();
    }
}
