package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RedstoneEmitterTileEntity();
    }
}
