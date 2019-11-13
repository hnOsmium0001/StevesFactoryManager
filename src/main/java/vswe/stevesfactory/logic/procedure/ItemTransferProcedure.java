package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.item.*;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.DirectionSelectionMenu;
import vswe.stevesfactory.ui.manager.menu.InventorySelectionMenu;
import vswe.stevesfactory.utils.IOHelper;

import java.util.*;

public class ItemTransferProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget, IItemFilterTarget {

    public static final int SOURCE_INVENTORIES = 0;
    public static final int DESTINATION_INVENTORIES = 1;
    public static final int FILTER = 0;

    private List<BlockPos> sourceInventories = new ArrayList<>();
    private Set<Direction> sourceDirections = EnumSet.noneOf(Direction.class);
    private List<BlockPos> destinationInventories = new ArrayList<>();
    private Set<Direction> destinationDirections = EnumSet.noneOf(Direction.class);
    private IItemFilter filter = new ItemTraitsFilter();

    private List<LazyOptional<IItemHandler>> cachedSourceCaps = new ArrayList<>();
    private List<LazyOptional<IItemHandler>> cachedDestinationCaps = new ArrayList<>();

    public ItemTransferProcedure() {
        super(Procedures.ITEM_TRANSFER.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
        if (hasError()) {
            return;
        }

        // TODO invalidate cache based on dirty checking
        cacheCaps(context);

        List<SingleItemBufferElement> items = new ArrayList<>();
        for (LazyOptional<IItemHandler> cap : cachedSourceCaps) {
            if (cap.isPresent()) {
                IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                filter.extractFromInventory((stack, slot) -> items.add(new SingleItemBufferElement(stack, handler, slot)), handler);
            }
        }

        for (LazyOptional<IItemHandler> cap : cachedDestinationCaps) {
            if (cap.isPresent()) {
                IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                // We don't need filter here because this is just in one procedure
                // It does not make sense to have multiple filters for one item transferring step
                for (SingleItemBufferElement buffer : items) {
                    ItemStack source = buffer.stack;
                    if (source.isEmpty()) {
                        continue;
                    }
                    int sourceStackSize = source.getCount();
                    ItemStack untaken = ItemHandlerHelper.insertItem(handler, source, false);

                    buffer.used += sourceStackSize - untaken.getCount();
                    buffer.stack = untaken;
                }
            }

        }

        for (SingleItemBufferElement buffer : items) {
            if (buffer.used > 0) {
                buffer.inventory.extractItem(buffer.slot, buffer.used, false);
            }
        }
    }

    public boolean hasError() {
        return sourceInventories.isEmpty() || sourceDirections.isEmpty()
                || destinationInventories.isEmpty() || destinationDirections.isEmpty();
    }

    private void cacheCaps(IExecutionContext context) {
        Set<BlockPos> linkedInventories = context.getController().getLinkedInventories(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        cachedSourceCaps.clear();
        for (BlockPos pos : sourceInventories) {
            // Don't force remove non-existing connections as a more user friendly design
            // so that in case player accidentally break a cable, the settings are still preserved
            // the player can just place the cable back and everything will function properly as before
            if (!linkedInventories.contains(pos)) {
                continue;
            }
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : sourceDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    cachedSourceCaps.add(cap);
                }
            }
        }
        cachedDestinationCaps.clear();
        for (BlockPos pos : destinationInventories) {
            if (!linkedInventories.contains(pos)) {
                continue;
            }
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : destinationDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    cachedDestinationCaps.add(cap);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<ItemTransferProcedure> createFlowComponent() {
        FlowComponent<ItemTransferProcedure> f = FlowComponent.of(this);
        f.addMenu(new InventorySelectionMenu<>(SOURCE_INVENTORIES, I18n.format("gui.sfm.Menu.InventorySelection.Source"), I18n.format("error.sfm.ItemTransfer.NoSrcInv"), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY));
        f.addMenu(new InventorySelectionMenu<>(DESTINATION_INVENTORIES, I18n.format("gui.sfm.Menu.InventorySelection.Destination"), I18n.format("error.sfm.ItemTransfer.NoSrcTarget"), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY));
        f.addMenu(new DirectionSelectionMenu<>(SOURCE_INVENTORIES, I18n.format("gui.sfm.Menu.TargetSides.Source"), I18n.format("error.sfm.ItemTransfer.NoDestInv")));
        f.addMenu(new DirectionSelectionMenu<>(DESTINATION_INVENTORIES, I18n.format("gui.sfm.Menu.TargetSides.Destination"), I18n.format("error.sfm.ItemTransfer.NoDestTarget")));
        IItemFilterTarget.createFilterMenu(this, f, FILTER);
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("SourcePoses", IOHelper.writeBlockPoses(sourceInventories));
        tag.putIntArray("SourceDirections", IOHelper.direction2Index(sourceDirections));
        tag.put("TargetPoses", IOHelper.writeBlockPoses(destinationInventories));
        tag.putIntArray("TargetDirections", IOHelper.direction2Index(destinationDirections));
        tag.put("Filter", IOHelper.writeItemFilter(filter));
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        sourceInventories = IOHelper.readBlockPoses(tag.getList("SourcePoses", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        sourceDirections = IOHelper.index2DirectionFill(tag.getIntArray("SourceDirections"), EnumSet.noneOf(Direction.class));
        destinationInventories = IOHelper.readBlockPoses(tag.getList("TargetPoses", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        destinationDirections = IOHelper.index2DirectionFill(tag.getIntArray("TargetDirections"), EnumSet.noneOf(Direction.class));
        filter = IOHelper.readItemFilter(tag.getCompound("Filter"));
    }

    @Override
    public IItemFilter getFilter() {
        return filter;
    }

    public void setFilter(IItemFilter filter) {
        this.filter = filter;
    }

    @Override
    public List<BlockPos> getInventories(int id) {
        switch (id) {
            case SOURCE_INVENTORIES:
            default:
                return sourceInventories;
            case DESTINATION_INVENTORIES: return destinationInventories;
        }
    }

    @Override
    public Set<Direction> getDirections(int id) {
        switch (id) {
            case SOURCE_INVENTORIES:
            default:
                return sourceDirections;
            case DESTINATION_INVENTORIES: return destinationDirections;
        }
    }

    @Override
    public IItemFilter getFilter(int id) {
        return filter;
    }

    @Override
    public void setFilter(int filterID, IItemFilter filter) {
        if (filterID == FILTER) {
            this.filter = filter;
        }
    }
}
