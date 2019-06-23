package vswe.stevesfactory.blocks.manager;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import vswe.stevesfactory.blocks.BaseBlock;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FactoryManagerBlock extends BaseBlock {

    public FactoryManagerBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult lookingAt) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof FactoryManagerTileEntity) {
                FactoryManagerTileEntity manager = (FactoryManagerTileEntity) tile;
                manager.openGUI(player);
                tryDump(manager);
            }
        } else {
            Minecraft.getInstance().displayGuiScreen(new FactoryManagerGUI());
        }
        return true;
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

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FactoryManagerTileEntity();
    }

}
