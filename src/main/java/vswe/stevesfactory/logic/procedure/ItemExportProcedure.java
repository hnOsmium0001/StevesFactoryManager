package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.item.IItemFilter;
import vswe.stevesfactory.logic.item.ItemTagFilter;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.DirectionSelectionMenu;
import vswe.stevesfactory.ui.manager.menu.InventorySelectionMenu;
import vswe.stevesfactory.ui.manager.menu.ItemTagFilterMenu;
import vswe.stevesfactory.utils.IOHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemExportProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget, IItemFilterTarget {

    public static final int INVENTORIES = 0;
    public static final int FILTER = 0;

    private List<BlockPos> inventories = new ArrayList<>();
    private List<Direction> directions = new ArrayList<>();
    private IItemFilter filter = new ItemTagFilter();

    public ItemExportProcedure(CommandGraph graph) {
        super(Procedures.ITEM_EXPORT.getFactory(), graph);
    }

    public ItemExportProcedure(CommandGraph graph, int possibleParents, int possibleChildren) {
        super(Procedures.ITEM_EXPORT.getFactory(), graph, possibleParents, possibleChildren);
    }

    public ItemExportProcedure(INetworkController controller) {
        super(Procedures.ITEM_EXPORT.getFactory(), controller);
    }

    public ItemExportProcedure(INetworkController controller, int possibleParents, int possibleChildren) {
        super(Procedures.ITEM_EXPORT.getFactory(), controller, possibleParents, possibleChildren);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    public FlowComponent<ItemExportProcedure> createFlowComponent() {
        FlowComponent<ItemExportProcedure> f = FlowComponent.of(this);
        f.addMenu(new InventorySelectionMenu<>(INVENTORIES, "gui.sfm.Menu.InventorySelection"));
        f.addMenu(new DirectionSelectionMenu<>(INVENTORIES, "gui.sfm.Menu.TargetSides"));
        IItemFilterTarget.createFilterMenu(this, f, FILTER);
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("Inventories", IOHelper.writeBlockPoses(inventories));
        tag.putIntArray("Directions", IOHelper.direction2Index(directions));
        tag.put("Filter", filter.write());
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        inventories = IOHelper.readBlockPoses(tag.getList("Inventories", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        directions = IOHelper.index2Direction(tag.getIntArray("Directions"));
    }

    @Override
    public List<Direction> getDirections(int id) {
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
