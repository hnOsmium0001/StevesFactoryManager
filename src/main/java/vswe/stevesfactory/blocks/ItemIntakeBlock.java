package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vswe.stevesfactory.network.PacketOpenGUI;

import java.util.function.Supplier;

public class ItemIntakeBlock extends Block {

    public static final EnumProperty<ItemIntakeTileEntity.Mode> MODE_PROPERTY = EnumProperty.create("mode", ItemIntakeTileEntity.Mode.class);

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

        if (player.isSneaking()) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ItemIntakeTileEntity) {
            ItemIntakeTileEntity intake = (ItemIntakeTileEntity) tile;
            PacketOpenGUI.openItemIntake((ServerPlayerEntity) player, world.dimension.getType(), pos, intake.write(new CompoundNBT()));
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
        builder.add(BlockStateProperties.FACING, MODE_PROPERTY);
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
