package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import vswe.stevesfactory.ui.intake.ItemIntakeContainer;
import vswe.stevesfactory.utils.Utils;

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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ItemIntakeTileEntity) {
            ItemIntakeTileEntity intake = (ItemIntakeTileEntity) tile;
            ItemIntakeContainer.openGUI((ServerPlayerEntity) player, intake);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState replacedBy, boolean moved) {
        if (state.getBlock() != replacedBy.getBlock()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof ItemIntakeTileEntity) {
                tile
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .ifPresent(inventory -> Utils.dropInventoryItems(world, pos, inventory));
            }
            super.onReplaced(state, world, pos, replacedBy, moved);
        }
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
