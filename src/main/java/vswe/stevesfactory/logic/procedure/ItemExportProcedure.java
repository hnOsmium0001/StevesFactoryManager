package vswe.stevesfactory.logic.procedure;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.*;
import vswe.stevesfactory.logic.item.IItemFilter;
import vswe.stevesfactory.logic.item.ItemTraitsFilter;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.DirectionSelectionMenu;
import vswe.stevesfactory.ui.manager.menu.InventorySelectionMenu;
import vswe.stevesfactory.utils.IOHelper;

import java.util.*;

public class ItemExportProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget, IItemFilterTarget {

    public static final int INVENTORIES = 0;
    public static final int FILTER = 0;

    private List<BlockPos> inventories = new ArrayList<>();
    private Set<Direction> directions = EnumSet.noneOf(Direction.class);
    private IItemFilter filter = new ItemTraitsFilter();

    public ItemExportProcedure() {
        super(Procedures.ITEM_EXPORT.getFactory());
        filter.setType(FilterType.BLACKLIST);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
        if (hasError()) {
            return;
        }

        Set<BlockPos> linkedInventories = context.getController().getLinkedInventories(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        IWorld world = context.getControllerWorld();
        for (BlockPos pos : inventories) {
            if (!linkedInventories.contains(pos)) {
                continue;
            }
            TileEntity tile = world.getTileEntity(pos);
            if (tile == null) {
                continue;
            }

            for (Direction direction : directions) {
                LazyOptional<IItemHandler> cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);
                if (cap.isPresent()) {
                    IItemHandler handler = cap.orElseThrow(RuntimeException::new);
                    context.forEachItemBuffer((item, buffer) -> {
                        ItemStack bufferedStack = buffer.getStack();
                        if (!filter.test(bufferedStack)) {
                            return;
                        }
                        if (bufferedStack.isEmpty()) {
                            return;
                        }

                        // Simulate limit input stack size
                        int sourceCount = bufferedStack.getCount();
                        int need = Math.min(calculateNeededAmount(handler, bufferedStack), sourceCount);
                        if (need == 0) {
                            return;
                        }
                        bufferedStack.setCount(need);

                        ItemStack untaken = ItemHandlerHelper.insertItem(handler, bufferedStack, false);
                        int takenCount = need - untaken.getCount();
                        int untakenCount = sourceCount - takenCount;

                        buffer.use(takenCount);
                        // Reuse stack object
                        untaken.setCount(untakenCount);
                        buffer.setStack(untaken);
                    });
                }
            }
        }
    }

    private int calculateNeededAmount(IItemHandler handler, ItemStack source) {
        if (!this.filter.isMatchingAmount()) {
            return source.getCount();
        }
        int totalCount = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (source.isItemEqual(stack)) {
                totalCount += stack.getCount();
            }
        }
        return filter.limitFlowRate(source, totalCount);
    }

    public boolean hasError() {
        return inventories.isEmpty() || directions.isEmpty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<ItemExportProcedure> createFlowComponent() {
        FlowComponent<ItemExportProcedure> f = FlowComponent.of(this);
        f.addMenu(new InventorySelectionMenu<>(INVENTORIES, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY));
        f.addMenu(new DirectionSelectionMenu<>(INVENTORIES));
        IItemFilterTarget.createFilterMenu(this, f, FILTER);
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("Inventories", IOHelper.writeBlockPoses(inventories));
        tag.putIntArray("Directions", IOHelper.direction2Index(directions));
        tag.put("Filter", IOHelper.writeItemFilter(filter));
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        inventories = IOHelper.readBlockPoses(tag.getList("Inventories", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        directions = IOHelper.index2DirectionFill(tag.getIntArray("Directions"), EnumSet.noneOf(Direction.class));
        filter = IOHelper.readItemFilter(tag.getCompound("Filter"));
    }

    @Override
    public Set<Direction> getDirections(int id) {
        return directions;
    }

    @Override
    public List<BlockPos> getInventories(int id) {
        return inventories;
    }

    @Override
    public IItemFilter getFilter(int id) {
        return filter;
    }

    @Override
    public void setFilter(int id, IItemFilter filter) {
        if (id == FILTER) {
            this.filter = filter;
        }
    }
}
