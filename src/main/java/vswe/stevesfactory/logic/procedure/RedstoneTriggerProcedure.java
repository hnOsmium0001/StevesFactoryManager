package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.*;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.execution.ProcedureExecutor;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.*;
import vswe.stevesfactory.utils.IOHelper;
import vswe.stevesfactory.utils.Utils;

import java.util.*;

public class RedstoneTriggerProcedure extends AbstractProcedure implements ITrigger, IInventoryTarget, IDirectionTarget, ILogicalConjunction, IAnalogTarget {

    public static final int INVENTORIES = 0;
    public static final int DIRECTIONS = 0;

    public static final int HIGH_SUCCESSOR = 0;
    public static final int LOW_SUCCESSOR = 1;

    private List<BlockPos> watchingSources = new ArrayList<>();
    private Set<Direction> directions = EnumSet.allOf(Direction.class);
    private Type conjunction = Type.ANY;
    private int analogBegin = 1;
    private int analogEnd = 15;
    private boolean invertCondition = false;

    // Do not cache caps because we use it once, to set listeners for redstone changes
    // However we still want the dirty check to update listeners upon data mutation
    private boolean dirty = true;

    public RedstoneTriggerProcedure() {
        super(Procedures.REDSTONE_TRIGGER.getFactory(), 0, 2);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, HIGH_SUCCESSOR);
    }

    @Override
    public void tick(INetworkController controller) {
        if (!dirty) {
            return;
        }

        for (BlockPos watching : watchingSources) {
            World world = controller.getControllerWorld();
            TileEntity tile = world.getTileEntity(watching);
            if (tile != null) {
                LazyOptional<IRedstoneEventBus> cap = tile.getCapability(CapabilityRedstoneEventBus.REDSTONE_EVENT_BUS_CAPABILITY);
                if (cap.isPresent()) {
                    cap.orElseThrow(RuntimeException::new).subscribeEvent(status -> {
                        // If this procedure is invalid, which means it was removed from the controller, remove the event handler
                        if (!this.isValid()) {
                            return true;
                        }
                        // Actual triggering logic
                        // If is high powered, don't check for low power, and vice versa
                        if (test(status, !status.hasSignal())) {
                            // TODO consider moving this to #execute(IExecutionContext)
                            Connection connection = successors()[status.hasSignal() ? HIGH_SUCCESSOR : LOW_SUCCESSOR];
                            if (connection != null) {
                                new ProcedureExecutor(controller, world).start(connection.getDestination());
                            }
                        }
                        return false;
                    });
                }
            }
        }
        dirty = false;
    }

    private boolean test(SignalStatus status, boolean checkLowPower) {
        boolean result = conjunction == Type.ALL;
        for (Direction direction : directions) {
            int power = status.get(direction);
            result = conjunction.combine(result, Utils.invertIf(analogTest(power), checkLowPower));
        }
        return result;
    }

    @Override
    public FlowComponent<RedstoneTriggerProcedure> createFlowComponent() {
        FlowComponent<RedstoneTriggerProcedure> f = FlowComponent.of(this, 0, 2);
        f.addMenu(new InventorySelectionMenu<>(INVENTORIES, I18n.format("gui.sfm.Menu.RedstoneTrigger.Watches"), I18n.format("error.sfm.RedstoneTrigger.NoWatches"), CapabilityRedstoneEventBus.REDSTONE_EVENT_BUS_CAPABILITY));
        f.addMenu(new RedstoneSidesMenu<>(DIRECTIONS,
                () -> conjunction == Type.ANY, () -> conjunction = Type.ANY, I18n.format("gui.sfm.Menu.IfAny"),
                () -> conjunction == Type.ALL, () -> conjunction = Type.ALL, I18n.format("gui.sfm.Menu.RequireAll"),
                I18n.format("gui.sfm.Menu.RedstoneTrigger.Sides"), I18n.format("gui.sfm.Menu.RedstoneTrigger.Sides.Info")));
        f.addMenu(new RedstoneStrengthMenu<>());
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("Watching", IOHelper.writeBlockPoses(watchingSources));
        tag.putIntArray("Directions", IOHelper.direction2Index(directions));
        tag.putInt("ConjunctionType", conjunction.ordinal());
        tag.putInt("AnalogBegin", analogBegin);
        tag.putInt("AnalogEnd", analogEnd);
        tag.putBoolean("InvertCondition", invertCondition);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        watchingSources = IOHelper.readBlockPoses(tag.getList("Watching", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        directions = IOHelper.index2DirectionFill(tag.getIntArray("Directions"), EnumSet.noneOf(Direction.class));
        conjunction = Type.VALUES[tag.getInt("ConjunctionType")];
        analogBegin = tag.getInt("AnalogBegin");
        analogEnd = tag.getInt("AnalogEnd");
        invertCondition = tag.getBoolean("InvertCondition");
        markDirty();
    }

    @Override
    public List<BlockPos> getInventories(int id) {
        return watchingSources;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public Set<Direction> getDirections(int id) {
        return directions;
    }

    @Override
    public Type getConjunction() {
        return conjunction;
    }

    @Override
    public void setConjunction(Type type) {
        conjunction = type;
    }

    @Override
    public int getAnalogBegin() {
        return analogBegin;
    }

    @Override
    public void setAnalogBegin(int analogBegin) {
        this.analogBegin = analogBegin;
    }

    @Override
    public int getAnalogEnd() {
        return analogEnd;
    }

    @Override
    public void setAnalogEnd(int analogEnd) {
        this.analogEnd = analogEnd;
    }

    @Override
    public boolean isInverted() {
        return invertCondition;
    }

    @Override
    public void setInverted(boolean inverted) {
        invertCondition = inverted;
    }

    private boolean analogTest(int power) {
        return Utils.invertIf(power >= analogBegin && power <= analogEnd, invertCondition);
    }
}
