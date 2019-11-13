package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import vswe.stevesfactory.api.capability.CapabilityRedstone;
import vswe.stevesfactory.api.capability.IRedstoneHandler;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.Procedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.*;
import vswe.stevesfactory.utils.IOHelper;

import java.util.*;

// TODO cache caps
public class RedstoneEmitterProcedure extends AbstractProcedure implements IInventoryTarget, IDirectionTarget {

    public static final int EMITTERS = 0;
    public static final int SIDES = 0;

    private List<BlockPos> emitters = new ArrayList<>();
    private Set<Direction> directions = EnumSet.allOf(Direction.class);
    private IRedstoneHandler.Type signalType = IRedstoneHandler.Type.WEAK;
    private OperationType operationType = OperationType.FIXED;

    private int value = 15;

    public RedstoneEmitterProcedure() {
        super(Procedures.REDSTONE_EMITTER.getFactory());
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
        if (hasError()) {
            return;
        }

        for (BlockPos emitter : emitters) {
            TileEntity tile = context.getControllerWorld().getTileEntity(emitter);
            if (tile == null) {
                continue;
            }
            for (Direction direction : directions) {
                tile.getCapability(CapabilityRedstone.REDSTONE_CAPABILITY, direction).ifPresent(redstone -> {
                    redstone.setType(signalType);
                    switch (operationType) {
                        case FIXED:
                            redstone.setSignal(value);
                            break;
                        case TOGGLE:
                            int power = redstone.getSignal();
                            redstone.setSignal(power > 0 ? 0 : value);
                            break;
//                        case FORWARD:
//                            break;
//                        case BACKWARD:
//                            break;
                        case MIN:
                            redstone.setSignal(Math.min(redstone.getSignal(), value));
                            break;
                        case MAX:
                            redstone.setSignal(Math.max(redstone.getSignal(), value));
                            break;
                        case INCREASE:
                            redstone.setSignal(redstone.getSignal() + value);
                            break;
                        case DECREASE:
                            redstone.setSignal(redstone.getSignal() - value);
                            break;
                    }
                });
            }
        }
    }

    public boolean hasError() {
        return emitters.isEmpty() || directions.isEmpty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<RedstoneEmitterProcedure> createFlowComponent() {
        FlowComponent<RedstoneEmitterProcedure> f = FlowComponent.of(this);
        f.addMenu(new InventorySelectionMenu<>(EMITTERS, I18n.format("gui.sfm.Menu.RedstoneEmitter.Emitters"), I18n.format("error.sfm.RedstoneEmitter.NoEmitters"), CapabilityRedstone.REDSTONE_CAPABILITY));
        f.addMenu(new RedstoneSidesMenu<>(SIDES,
                () -> signalType == IRedstoneHandler.Type.WEAK, () -> signalType = IRedstoneHandler.Type.WEAK, I18n.format("gui.sfm.Menu.WeakRedstoneSignal"),
                () -> signalType == IRedstoneHandler.Type.STRONG, () -> signalType = IRedstoneHandler.Type.STRONG, I18n.format("gui.sfm.Menu.StrongRedstoneSignal"),
                I18n.format("gui.sfm.Menu.RedstoneEmitter.Sides"), I18n.format("gui.sfm.Menu.RedstoneEmitter.Sides.Info")));
        f.addMenu(new EmitterTypeMenu());
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.put("Emitters", IOHelper.writeBlockPoses(emitters));
        tag.putIntArray("Directions", IOHelper.direction2Index(directions));
        tag.putInt("OperationType", operationType.ordinal());
        tag.putInt("SignalType", signalType.ordinal());
        tag.putInt("Value", value);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        emitters = IOHelper.readBlockPoses(tag.getList("Emitters", Constants.NBT.TAG_COMPOUND), new ArrayList<>());
        directions = IOHelper.index2DirectionFill(tag.getIntArray("Directions"), EnumSet.noneOf(Direction.class));
        operationType = OperationType.VALUES[tag.getInt("OperationType")];
        signalType = IRedstoneHandler.Type.VALUES[tag.getInt("SignalType")];
        value = tag.getInt("Value");
    }

    @Override
    public List<BlockPos> getInventories(int id) {
        return emitters;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public Set<Direction> getDirections(int id) {
        return directions;
    }

    public enum OperationType {
        FIXED("Fixed"),
        TOGGLE("Toggle"),
        FORWARD("Forward"),
        BACKWARD("Backward"),
        MIN("Min"),
        MAX("Max"),
        INCREASE("Increase"),
        DECREASE("Decrease");

        public final String nameKey;
        public final String descriptionKey;

        OperationType(String nameKey) {
            this.nameKey = "gui.sfm.Menu.RedstoneEmitter.Type." + nameKey;
            this.descriptionKey = this.nameKey + ".Info";
        }

        public static final OperationType[] VALUES = values();
    }
}
