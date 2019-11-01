package vswe.stevesfactory.blocks;

import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.NetworkHelper;

public class CableTileEntity extends BaseTileEntity implements ICable {

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public void addLinksFor(INetworkController controller) {
        NetworkHelper.updateLinksFor(controller, this);
    }

    @Override
    public boolean isCable() {
        return true;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }
}
