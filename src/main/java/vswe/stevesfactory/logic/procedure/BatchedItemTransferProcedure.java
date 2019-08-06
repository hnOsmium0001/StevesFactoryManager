package vswe.stevesfactory.logic.procedure;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.utils.IOHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BatchedItemTransferProcedure extends AbstractProcedure {

    private List<BlockPos> sourceInventories;
    private List<Direction> sourceDirections;
    private List<BlockPos> targetInventories;
    private List<Direction> targetDirections;

    public BatchedItemTransferProcedure(INetworkController controller) {
        super(controller, 1);
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {
        // TODO complete
        // TODO wrap with slotless item handler
        List<ItemStack> extractableItems = new ArrayList<>();
        for (BlockPos pos : sourceInventories) {
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : sourceDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                    for (int i = 0; i < handler.getSlots(); i++) {
                        // TODO filter
                        extractableItems.add(handler.extractItem(i, 64, true));
                    }
                }
            }
        }

        List<ItemStack> transferedItems = new ArrayList<>();
        for (BlockPos pos : targetInventories) {
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : targetDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                    // TODO fill
                }
            }
        }

        return nexts()[0];
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();

        tag.put("SourcePoses", IOHelper.writeBlockPoses(sourceInventories));
        tag.putIntArray("SourceDirections", IOHelper.direction2Index(sourceDirections));
        tag.put("TargetPoses", IOHelper.writeBlockPoses(targetInventories));
        tag.putIntArray("TargetDirections", IOHelper.direction2Index(targetDirections));

        return tag;
    }
}
