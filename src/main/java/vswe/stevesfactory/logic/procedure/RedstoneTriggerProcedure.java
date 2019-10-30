package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.api.capability.CapabilitySignalReactor;
import vswe.stevesfactory.api.capability.SignalStatus;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.logic.execution.ProcedureExecutor;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.*;
import vswe.stevesfactory.utils.IOHelper;
import vswe.stevesfactory.utils.Utils;

import javax.annotation.Nullable;
import java.util.*;

public class RedstoneTriggerProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget, ILogicalConjunction, IAnalogTarget {

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

    private boolean reload = true;

    public RedstoneTriggerProcedure() {
        super(Procedures.REDSTONE_TRIGGER.getFactory(), 0, 2);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, HIGH_SUCCESSOR);
    }

    @Override
    public void tick() {
        if (reload) {
            for (BlockPos watching : watchingSources) {
                World world = getController().getControllerWorld();
                TileEntity tile = world.getTileEntity(watching);
                if (tile != null) {
                    tile.getCapability(CapabilitySignalReactor.SIGNAL_REACTOR_CAPABILITY)
                            .ifPresent(cap -> cap.subscribeEvent(status -> {
                                // If this procedure is invalid, which means it was removed from the controller, remove the event handler
                                if (!this.isValid()) {
                                    return true;
                                }
                                // Actual triggering logic
                                // If is high powered, don't check for low power, and vice versa
                                if (test(status, !status.hasSignal())) {
                                    execute(successors()[status.hasSignal() ? HIGH_SUCCESSOR : LOW_SUCCESSOR]);
                                }
                                return false;
                            }));
                }
            }
            reload = false;
        }
    }

    private boolean test(SignalStatus status, boolean checkLowPower) {
        boolean result = conjunction == Type.ALL;
        for (Direction direction : directions) {
            int power = status.get(direction);
            result = conjunction.combine(result, Utils.invertIf(analogTest(power), checkLowPower));
        }
        return result;
    }

    private void execute(@Nullable Connection connection) {
        if (connection != null) {
            execute(connection.getDestination());
        }
    }

    private void execute(IProcedure child) {
        INetworkController controller = this.getController();
        new ProcedureExecutor(controller, controller.getControllerWorld()).start(child);
    }

    @Override
    public FlowComponent<RedstoneTriggerProcedure> createFlowComponent() {
        FlowComponent<RedstoneTriggerProcedure> f = FlowComponent.of(this, 0, 2);
        f.addMenu(new InventorySelectionMenu<>(INVENTORIES, I18n.format("gui.sfm.Menu.RedstoneTrigger.Watches"), I18n.format("error.sfm.RedstoneTrigger.NoWatches"), CapabilitySignalReactor.SIGNAL_REACTOR_CAPABILITY));
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
        reload = true;
    }

    @Override
    public List<BlockPos> getInventories(int id) {
        return watchingSources;
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
    public void setAnalogRange(int begin, int end) {
        analogBegin = begin;
        analogEnd = end;
    }

    @Override
    public int getAnalogBegin() {
        return analogBegin;
    }

    @Override
    public int getAnalogEnd() {
        return analogEnd;
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
