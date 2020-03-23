package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class FactoryManagerBlock extends Block {

    public FactoryManagerBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult lookingAt) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof FactoryManagerTileEntity) {
                FactoryManagerTileEntity manager = (FactoryManagerTileEntity) tile;
                if (player.isCrouching()) {
                    tryDump(manager);
                } else {
                    manager.activate(player);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    private void tryDump(FactoryManagerTileEntity manager) {
        if (LogManager.getRootLogger().getLevel().isLessSpecificThan(Level.DEBUG)) {
            manager.dump();
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FactoryManagerTileEntity();
    }
}
