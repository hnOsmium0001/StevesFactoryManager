package vswe.stevesfactory.blocks.cable;

import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.api.ICable;
import vswe.stevesfactory.setup.ModBlocks;

public class CableTileEntity extends TileEntity implements ICable {

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
