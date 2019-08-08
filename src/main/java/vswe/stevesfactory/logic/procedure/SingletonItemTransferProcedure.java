package vswe.stevesfactory.logic.procedure;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.utils.IOHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SingletonItemTransferProcedure extends AbstractProcedure {

    private BlockPos sourcePos;
    private List<Direction> sourceDirections = new ArrayList<>();
    private BlockPos targetPos;
    private List<Direction> targetDirections = new ArrayList<>();

    public SingletonItemTransferProcedure(INetworkController controller) {
        super(Procedures.SINGLETON_ITEM_TRANSFER.getFactory(), controller, 1);
    }

    @Nullable
    @Override
    public IProcedure execute(IExecutionContext context) {

        // TODO update logic to fix issues
        IProcedure next = nexts()[0];

        // TODO port with SlotlessItemHandler
        List<ItemStack> extractableItems = new ArrayList<>();
        TileEntity source = context.getControllerWorld().getTileEntity(this.sourcePos);
        if (source == null) {
            return next;
        }
        TileEntity target = context.getControllerWorld().getTileEntity(this.targetPos);
        if (target == null) {
            return next;
        }

        for (Direction direction : sourceDirections) {
            LazyOptional<IItemHandler> cap = source.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
            if (cap.isPresent()) {
                IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                for (int i = 0; i < handler.getSlots(); i++) {
                    // TODO filter
                    extractableItems.add(handler.extractItem(i, 64, true));
                }
            }
        }

        // Note that after this step the list extractableItems may contain empty stacks
        List<ItemStack> transferredItems = new ArrayList<>();
        for (Direction direction : targetDirections) {
            LazyOptional<IItemHandler> cap = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
            if (cap.isPresent()) {
                IItemHandler handler = cap.orElseThrow(RuntimeException::new);

                // Try to insert every possible source stack
                for (int i = 0; i < extractableItems.size(); i++) {
                    ItemStack available = extractableItems.get(i);
                    if (available.isEmpty()) {
                        continue;
                    }
                    // ...into each slot
                    // TODO filter
                    for (int s = 0; s < handler.getSlots(); s++) {
                        ItemStack untaken = handler.insertItem(s, available, false);
                        extractableItems.set(i, untaken);
                    }
                }
            }
        }

        // Actually remove the items from the source container
        for (Direction direction : sourceDirections) {
            LazyOptional<IItemHandler> cap = source.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
            if (cap.isPresent()) {
                IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                for (ItemStack stack : transferredItems) {
                    if (stack.isEmpty()) {
                        continue;
                    }

                    for (int i = 0; i < handler.getSlots(); i++) {
                        int amountRemoved = handler.extractItem(i, stack.getCount(), false).getCount();
                        stack.shrink(amountRemoved);
                    }
                }
            }
        }

        return next;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();

        tag.put("SourcePos", NBTUtil.writeBlockPos(sourcePos));
        tag.putIntArray("SourceDirections", IOHelper.direction2Index(targetDirections));
        tag.put("TargetPos", NBTUtil.writeBlockPos(targetPos));
        tag.putIntArray("TargetDirections", IOHelper.direction2Index(targetDirections));

        return tag;
    }

    public static SingletonItemTransferProcedure deserialize(CompoundNBT tag) {
        SingletonItemTransferProcedure p = new SingletonItemTransferProcedure(readController(tag));

        p.sourcePos = NBTUtil.readBlockPos(tag.getCompound("SourcePos"));
        p.sourceDirections = IOHelper.index2Direction(tag.getIntArray("SourceDirections"));
        p.targetPos = NBTUtil.readBlockPos(tag.getCompound("TargetPos"));
        p.targetDirections = IOHelper.index2Direction(tag.getIntArray("TargetDirections"));

        return p;
    }

    public static FlowComponent createFlowComponent(SingletonItemTransferProcedure procedure) {
        return Procedures.SINGLETON_ITEM_TRANSFER.factory.createWidgetDefault(procedure);
    }
}
