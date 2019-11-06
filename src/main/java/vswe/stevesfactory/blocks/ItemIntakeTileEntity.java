package vswe.stevesfactory.blocks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import vswe.stevesfactory.Config;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.utils.NetworkHelper;

public class ItemIntakeTileEntity extends TileEntity implements ITickableTileEntity, ICable {

    public enum Mode {
        FRONT("message.sfm.ItemIntake.Mode.Front"),
        CENTERED("message.sfm.ItemIntake.Mode.Centered");

        public final String nameKey;

        Mode(String nameKey) {
            this.nameKey = nameKey;
        }

        public static final Mode[] VALUES = values();
    }

    public static ItemIntakeTileEntity regular() {
        return new ItemIntakeTileEntity(ModBlocks.itemIntakeTileEntity,
                Config.COMMON.isItemIntakeBlockCables,
                Config.COMMON.regularPickupInterval,
                Config.COMMON.regularMaxPickupDistance);
    }

    public static ItemIntakeTileEntity instant() {
        return new ItemIntakeTileEntity(ModBlocks.instantItemIntakeTileEntity,
                Config.COMMON.isInstantItemIntakeBlockCables,
                Config.COMMON.instantPickupInterval,
                Config.COMMON.instantMaxPickupDistance);
    }

    private final ForgeConfigSpec.BooleanValue isCable;
    private final ForgeConfigSpec.IntValue pickupInterval;
    private final ForgeConfigSpec.IntValue maxPickupDistance;

    private Mode mode = Mode.FRONT;

    private int ticks;

    private ItemIntakeTileEntity(TileEntityType<?> tileEntityTypeIn, ForgeConfigSpec.BooleanValue isCable, ForgeConfigSpec.IntValue pickupInterval, ForgeConfigSpec.IntValue maxPickupDistance) {
        super(tileEntityTypeIn);
        this.isCable = isCable;
        this.pickupInterval = pickupInterval;
        this.maxPickupDistance = maxPickupDistance;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ticks = 0;
    }

    @Override
    public void tick() {
        assert world != null;
        if (world.isRemote) {
            if (ticks == 0) {
                collectItems();
                ticks = pickupInterval.get();
            } else {
                ticks--;
            }
        }
    }

    private void collectItems() {
        // TODO
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void cycleMode() {
        int next = mode.ordinal() + 1;
        mode = next >= Mode.VALUES.length ? Mode.VALUES[0] : Mode.VALUES[next];
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean isCable() {
        return isCable.get();
    }

    @Override
    public void addLinksFor(INetworkController controller) {
        NetworkHelper.updateLinksFor(controller, this);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        tag.putInt("Mode", mode.ordinal());
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        mode = Mode.VALUES[tag.getInt("Mode")];
        return super.write(tag);
    }
}
