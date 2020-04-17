package vswe.stevesfactory.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import vswe.stevesfactory.Config;
import vswe.stevesfactory.api.network.ICable;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.render.IWorkingAreaProvider;
import vswe.stevesfactory.setup.ModBlocks;
import vswe.stevesfactory.ui.intake.ItemIntakeContainer;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemIntakeTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, ICable, IWorkingAreaProvider {

    public enum Mode implements IStringSerializable {
        FRONT("front", "gui.sfm.ItemIntake.Mode.Front"),
        CENTERED("centered", "gui.sfm.ItemIntake.Mode.Centered");

        public final String name;
        public final String statusTranslationKey;

        Mode(String name, String statusTranslationKey) {
            this.name = name;
            this.statusTranslationKey = statusTranslationKey;
        }

        @Override
        public String getName() {
            return name;
        }

        public static final Mode[] VALUES = values();
    }

    public static ItemIntakeTileEntity regular() {
        return new ItemIntakeTileEntity(ModBlocks.itemIntakeTileEntity.get()) {
            @Override
            public boolean isCable() {
                return Config.COMMON.isItemIntakeBlockCables.get();
            }

            @Override
            public boolean ignorePickupDelay() {
                return false;
            }

            @Override
            public int getInventorySize() {
                return Config.COMMON.regularInventorySize.get();
            }

            @Override
            public int getMaximumRadius() {
                return Config.COMMON.regularMaxRadius.get();
            }

            @Override
            protected int getPickupInterval() {
                return Config.COMMON.regularPickupInterval.get();
            }
        };
    }

    public static ItemIntakeTileEntity instant() {
        return new ItemIntakeTileEntity(ModBlocks.instantItemIntakeTileEntity.get()) {
            @Override
            public boolean isCable() {
                return Config.COMMON.isInstantItemIntakeBlockCables.get();
            }

            @Override
            public boolean ignorePickupDelay() {
                return true;
            }

            @Override
            public int getInventorySize() {
                return Config.COMMON.instantInventorySize.get();
            }

            @Override
            public int getMaximumRadius() {
                return Config.COMMON.instantMaxRadius.get();
            }

            @Override
            protected int getPickupInterval() {
                return Config.COMMON.instantPickupInterval.get();
            }
        };
    }

    private static final int STATE_READY = 0;
    private static final int STATE_RELOAD = -1;

    // Data and capabilities
    private LazyOptional<ItemStackHandler> invCap = LazyOptional.of(() -> new ItemStackHandler(getInventorySize()));
    private Mode mode = Mode.FRONT;
    private int radius = 0;
    private boolean rendering = false;

    // Tile entity/world state
    private AxisAlignedBB pickupBox = new AxisAlignedBB(BlockPos.ZERO);
    private int ticks;

    private ItemIntakeTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        ticks = STATE_RELOAD;
    }

    @Override
    public void tick() {
        assert world != null;
        if (!world.isRemote) {
            if (ticks == STATE_RELOAD) {
                reload();
                ticks = STATE_READY;
                return;
            }
            if (ticks == STATE_READY) {
                collectItems();
                ticks = getPickupInterval();
                return;
            }
            ticks--;
        }
    }

    public void reload() {
        setMode(mode);
        setRadius(radius);
        setRendering(rendering);
    }

    private void collectItems() {
        assert world != null;
        ItemStackHandler handler = invCap.orElseThrow(RuntimeException::new);
        List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, pickupBox);
        for (ItemEntity item : items) {
            if (!ignorePickupDelay() && item.cannotPickup()) {
                continue;
            }
            if (!item.isAlive()) {
                continue;
            }

            ItemStack leftover = ItemHandlerHelper.insertItem(handler, item.getItem(), false);
            if (leftover.isEmpty()) {
                item.remove();
            } else {
                item.setItem(leftover);
            }
        }
    }

    public abstract boolean ignorePickupDelay();

    public abstract int getInventorySize();

    public int getRadius() {
        return radius;
    }

    public abstract int getMaximumRadius();

    public void setRadius(int radius) {
        this.radius = MathHelper.clamp(radius, 0, getMaximumRadius());
        BlockState state = getBlockState();
        BlockPos origin = mode == Mode.CENTERED
                ? pos
                : pos.offset(state.get(BlockStateProperties.FACING), this.radius + 1);
        this.pickupBox = new AxisAlignedBB(origin).grow(this.radius);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        if (this.mode != mode) {
            assert world != null;
            this.mode = mode;
            world.setBlockState(pos, getBlockState().with(ItemIntakeBlock.MODE_PROPERTY, mode));
            // Update pickup box
            setRadius(radius);
        }
    }

    public void cycleMode() {
        int next = mode.ordinal() + 1;
        int max = Mode.VALUES.length;
        setMode(Mode.VALUES[next >= max ? 0 : next]);
    }

    protected abstract int getPickupInterval();

    @Override
    public AxisAlignedBB getWorkingArea() {
        return pickupBox;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return pickupBox;
    }

    @Override
    public boolean isRendering() {
        return rendering;
    }

    public void setRendering(boolean rendering) {
        this.rendering = rendering;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public void addLinksFor(INetworkController controller) {
        NetworkHelper.updateLinksFor(controller, this);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        readCustom(tag);
    }

    public void readCustom(CompoundNBT tag) {
        // Just retrieve data, update world state when chunk is loaded, in #tick()
        mode = Mode.VALUES[tag.getInt("Mode")];
        radius = tag.getInt("Radius");
        rendering = tag.getBoolean("Rendering");
        invCap.ifPresent(inv -> inv.deserializeNBT(tag.getCompound("Inventory")));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        writeCustom(tag);
        return super.write(tag);
    }

    public CompoundNBT writeCustom(CompoundNBT tag) {
        tag.putInt("Mode", mode.ordinal());
        tag.putInt("Radius", radius);
        tag.putBoolean("Rendering", rendering);
        invCap.map(ItemStackHandler::serializeNBT).ifPresent(data -> tag.put("Inventory", data));
        return tag;
    }

    @Override
    protected void invalidateCaps() {
        invCap.invalidate();
        super.invalidateCaps();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return invCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.sfm.Title.ItemIntake");
    }

    @Override
    public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
        return new ItemIntakeContainer(i, this);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    // Semi-equivalent of #onLoad for client-only purposes
    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        // Does TileEntity#read by default
        super.handleUpdateTag(tag);
        // This will always be on client, and world will always be ready
        reload();
    }
}
