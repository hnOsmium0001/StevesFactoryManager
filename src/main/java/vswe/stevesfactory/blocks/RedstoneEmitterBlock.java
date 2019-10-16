package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IStringSerializable;

public class RedstoneEmitterBlock extends BaseBlock {

    public enum Type implements IStringSerializable {
        WEAK("weak"),
        STRONG("strong");

        public final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static final EnumProperty<Type> TYPE_PROPERTY = EnumProperty.create("type", Type.class);

    public RedstoneEmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container) {
        // TODO each face has a separate type
        container.add(BlockStateProperties.FACING, TYPE_PROPERTY);
    }
}
