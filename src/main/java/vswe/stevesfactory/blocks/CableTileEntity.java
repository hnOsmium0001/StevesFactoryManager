package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.Utils;

public class CableTileEntity extends BaseTileEntity implements ICable {

    public CableTileEntity() {
        super(ModBlocks.cableTileEntity);
    }

    @Override
    public void addLinksFor(INetworkController controller) {
        for (Capability<?> cap : StevesFactoryManagerAPI.getRecognizableCapabilities()) {
            updateLinks(controller, cap);
        }
    }

    private void updateLinks(INetworkController controller, Capability<?> cap) {
        assert world != null;
        for (BlockPos neighbor : getNeighbors()) {
            TileEntity tile = world.getTileEntity(neighbor);
            if (tile == null) {
                continue;
            }
            if (Utils.hasCapabilityAtAll(tile, cap)) {
                controller.addLink(cap, neighbor);
            }
        }
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
