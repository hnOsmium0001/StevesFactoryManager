package vswe.stevesfactory.blocks.cable;

import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.api.ICable;
import vswe.stevesfactory.setup.ModBlocks;

import java.util.List;

public class CableTileEntity extends TileEntity implements ICable {

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    public List<ICable> getNeighbors() {
        return null;
    }

    @Override
    public boolean isCable() {
        return true;
    }

}
