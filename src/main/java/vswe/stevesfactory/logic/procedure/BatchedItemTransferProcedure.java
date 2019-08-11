package vswe.stevesfactory.logic.procedure;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.utils.IOHelper;
import vswe.stevesfactory.utils.SlotlessItemHandlerWrapper;

import java.util.ArrayList;
import java.util.List;

public class BatchedItemTransferProcedure extends AbstractProcedure {

    private List<BlockPos> sourceInventories = new ArrayList<>();
    private List<Direction> sourceDirections = new ArrayList<>();
    private List<BlockPos> targetInventories = new ArrayList<>();
    private List<Direction> targetDirections = new ArrayList<>();

    public BatchedItemTransferProcedure(INetworkController controller) {
        super(Procedures.BATCHED_ITEM_TRANSFER.getFactory(), controller, 1, 1);
    }

    @Override
    public void execute(IExecutionContext context) {
        List<TileEntity> sourceTiles = new ArrayList<>(sourceInventories.size());
        for (BlockPos pos : sourceInventories) {
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile != null) {
                sourceTiles.add(tile);
            }
        }

        List<ItemStack> extractableItems = new ArrayList<>();
        for (TileEntity tile : sourceTiles) {
            for (Direction direction : sourceDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    SlotlessItemHandlerWrapper handler = new SlotlessItemHandlerWrapper(cap.orElseThrow(RuntimeException::new));
                    // TODO filter
                    while (true) {
                        ItemStack src = handler.extractItem(Integer.MAX_VALUE, true);
                        if (src.isEmpty()) {
                            break;
                        }
                        extractableItems.add(src);
                    }
                }
            }
        }

        // Alias for the code to be understandable
        @SuppressWarnings("UnnecessaryLocalVariable") List<ItemStack> availableSourceItems = extractableItems;
        List<ItemStack> takenSourceItems = new ArrayList<>(availableSourceItems.size());
        for (ItemStack stack : availableSourceItems) {
            ItemStack c = stack.copy();
            // Start with no items taken. Note that this operation will only set the stack to empty, but keeping the item type
            c.setCount(0);
            takenSourceItems.add(c);
        }

        Preconditions.checkState(extractableItems.size() == takenSourceItems.size());

        for (BlockPos pos : targetInventories) {
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : targetDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    SlotlessItemHandlerWrapper handler = new SlotlessItemHandlerWrapper(cap.orElseThrow(RuntimeException::new));
                    // TODO filter
                    for (int i = 0; i < takenSourceItems.size(); i++) {
                        ItemStack source = takenSourceItems.get(i);
                        if (source.isEmpty()) {
                            continue;
                        }
                        int sourceStackSize = source.getCount();
                        ItemStack untaken = handler.insertItem(source, false);
                        takenSourceItems.set(i, untaken);
                        int taken = sourceStackSize - untaken.getCount();
                        availableSourceItems.get(i).grow(taken);
                    }
                }
            }
        }

        int flag = SlotlessItemHandlerWrapper.ITEM | SlotlessItemHandlerWrapper.DAMAGE | SlotlessItemHandlerWrapper.STACKSIZE;
        for (TileEntity tile : sourceTiles) {
            for (Direction direction : sourceDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    SlotlessItemHandlerWrapper handler = new SlotlessItemHandlerWrapper(cap.orElseThrow(RuntimeException::new));
                    for (ItemStack stack : takenSourceItems) {
                        if (stack.isEmpty()) {
                            continue;
                        }

                        ItemStack extracted = handler.extractItem(stack, flag, false);
                        stack.shrink(extracted.getCount());
                    }
                }
            }
        }

        context.push(successors()[0]);
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

    @Override
    public void deserialize(CommandGraph graph, CompoundNBT tag) {
        super.deserialize(graph, tag);
        sourceInventories = IOHelper.readBlockPoses(tag.getList("SourcePoses", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        sourceDirections = IOHelper.index2Direction(tag.getIntArray("SourceDirections"));
        targetInventories = IOHelper.readBlockPoses(tag.getList("TargetPoses", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        targetDirections = IOHelper.index2Direction(tag.getIntArray("TargetDirections"));
    }

    public static FlowComponent createFlowComponent(BatchedItemTransferProcedure procedure) {
        return Procedures.BATCHED_ITEM_TRANSFER.factory.createWidgetDefault(procedure);
    }
}
