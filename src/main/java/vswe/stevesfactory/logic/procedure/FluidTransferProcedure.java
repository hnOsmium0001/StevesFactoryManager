package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.item.IItemFilter;
import vswe.stevesfactory.logic.item.ItemTraitsFilter;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import java.util.*;

// TODO
public class FluidTransferProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget {

    private List<BlockPos> sourceInventories = new ArrayList<>();
    private Set<Direction> sourceDirections = EnumSet.noneOf(Direction.class);
    private List<BlockPos> destinationInventories = new ArrayList<>();
    private Set<Direction> destinationDirections = EnumSet.noneOf(Direction.class);
    private IItemFilter filter = new ItemTraitsFilter();

    private List<LazyOptional<IItemHandler>> cachedSourceCaps = new ArrayList<>();
    private List<LazyOptional<IItemHandler>> cachedDestinationCaps = new ArrayList<>();
    private boolean dirty = false;

    public FluidTransferProcedure() {
        super(Procedures.FLUID_TRANSFER.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<FluidTransferProcedure> createFlowComponent() {
        FlowComponent<FluidTransferProcedure> f = FlowComponent.of(this);
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        return super.serialize();
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        markDirty();
    }

    @Override
    public Set<Direction> getDirections(int id) {
        return null;
    }

    @Override
    public List<BlockPos> getInventories(int id) {
        return null;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }
}
