package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class FactoryManagerTileEntity extends TileEntity implements ITickableTileEntity {

    public FactoryManagerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {

    }

}
