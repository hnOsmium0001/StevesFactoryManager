package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import vswe.stevesfactory.setup.ModBlocks;

public class ItemIntakeTileEntity extends BaseTileEntity implements ITickableTileEntity {

    public static ItemIntakeTileEntity regular() {
        return new ItemIntakeTileEntity(ModBlocks.itemIntakeTileEntity);
    }

    public static ItemIntakeTileEntity instant() {
        return new ItemIntakeTileEntity(ModBlocks.instantItemIntakeTileEntity);
    }

    private ItemIntakeTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        assert world != null;
        if (world.isRemote) {

        }
    }
}
