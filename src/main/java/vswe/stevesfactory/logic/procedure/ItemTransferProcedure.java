package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.*;
import vswe.stevesfactory.logic.item.*;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.PropertyManager;
import vswe.stevesfactory.ui.manager.menu.*;
import vswe.stevesfactory.utils.IOHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemTransferProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget, IItemFilterTarget {

    public static final int SOURCE_INVENTORIES = 0;
    public static final int DESTINATION_INVENTORIES = 1;
    public static final int FILTER = 0;

    private List<BlockPos> sourceInventories = new ArrayList<>();
    private List<Direction> sourceDirections = new ArrayList<>();
    private List<BlockPos> targetInventories = new ArrayList<>();
    private List<Direction> targetDirections = new ArrayList<>();
    private IItemFilter filter = new ItemTraitsFilter();

    public ItemTransferProcedure(INetworkController controller) {
        super(Procedures.ITEM_TRANSFER.getFactory(), controller);
    }

    public ItemTransferProcedure(CommandGraph graph) {
        super(Procedures.ITEM_TRANSFER.getFactory(), graph);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
        if (hasError()) {
            return;
        }

        List<SingleItemBufferElement> items = new ArrayList<>();
        for (BlockPos pos : sourceInventories) {
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : sourceDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                    filter.extractFromInventory((stack, slot) -> items.add(new SingleItemBufferElement(stack, handler, slot)), handler);
                }
            }
        }

        for (BlockPos pos : targetInventories) {
            TileEntity tile = context.getControllerWorld().getTileEntity(pos);
            if (tile == null) {
                continue;
            }
            for (Direction direction : targetDirections) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
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
        }

        for (SingleItemBufferElement buffer : items) {
            if (buffer.used > 0) {
                buffer.inventory.extractItem(buffer.slot, buffer.used, false);
            }
        }
    }

    public boolean hasError() {
        return sourceInventories.isEmpty() || sourceDirections.isEmpty()
                || targetInventories.isEmpty() || targetDirections.isEmpty();
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("SourcePoses", IOHelper.writeBlockPoses(sourceInventories));
        tag.putIntArray("SourceDirections", IOHelper.direction2Index(sourceDirections));
        tag.put("TargetPoses", IOHelper.writeBlockPoses(targetInventories));
        tag.putIntArray("TargetDirections", IOHelper.direction2Index(targetDirections));
        tag.put("Filter", IOHelper.writeItemFilter(filter));
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        sourceInventories = IOHelper.readBlockPoses(tag.getList("SourcePoses", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        sourceDirections = IOHelper.index2Direction(tag.getIntArray("SourceDirections"));
        targetInventories = IOHelper.readBlockPoses(tag.getList("TargetPoses", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        targetDirections = IOHelper.index2Direction(tag.getIntArray("TargetDirections"));
        filter = IOHelper.readItemFilter(tag.getCompound("Filter"));
    }

    @Override
    public FlowComponent<ItemTransferProcedure> createFlowComponent() {
        FlowComponent<ItemTransferProcedure> f = FlowComponent.of(this);
        f.addMenu(new InventorySelectionMenu<>(SOURCE_INVENTORIES, I18n.format("gui.sfm.Menu.InventorySelection.Source")));
        f.addMenu(new InventorySelectionMenu<>(DESTINATION_INVENTORIES, I18n.format("gui.sfm.Menu.InventorySelection.Destination")));
        f.addMenu(new DirectionSelectionMenu<>(SOURCE_INVENTORIES, I18n.format("gui.sfm.Menu.TargetSides.Source")));
        f.addMenu(new DirectionSelectionMenu<>(DESTINATION_INVENTORIES, I18n.format("gui.sfm.Menu.TargetSides.Destination")));

        PropertyManager<IItemFilter, ItemTransferProcedure> pm = new PropertyManager<>(f, this::getFilter, this::setFilter);
        pm.on(filter -> filter instanceof ItemTraitsFilter)
                .name(I18n.format("gui.sfm.Menu.ItemFilter.Traits"))
                .prop(ItemTraitsFilter::new)
                .then(() -> new ItemTraitsFilterMenu<>(FILTER, I18n.format("gui.sfm.Menu.ItemFilter.Traits")));
        pm.on(filter -> filter instanceof ItemTagFilter)
                .name(I18n.format("gui.sfm.Menu.ItemFilter.Tags"))
                .prop(ItemTagFilter::new)
                .then(() -> new ItemTagFilterMenu<>(FILTER, I18n.format("gui.sfm.Menu.ItemFilter.Tags")));
        pm.actionCycling();
        pm.setProperty(filter);
        return f;
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
            case DESTINATION_INVENTORIES: return targetInventories;
        }
    }

    @Override
    public List<Direction> getDirections(int id) {
        switch (id) {
            case SOURCE_INVENTORIES:
            default:
                return sourceDirections;
            case DESTINATION_INVENTORIES: return targetDirections;
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
