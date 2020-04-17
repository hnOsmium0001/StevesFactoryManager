package vswe.stevesfactory.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import vswe.stevesfactory.api.capability.CapabilityDocuments;
import vswe.stevesfactory.api.capability.ITextDocument;
import vswe.stevesfactory.setup.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class SignUpdaterTileEntity extends TileEntity implements ITextDocument {

    private static final ITextComponent EMPTY_TEXT_COMPONENT = new StringTextComponent("");

    private LazyOptional<ITextDocument> textDisplayCap = LazyOptional.of(() -> this);
    private SignTileEntity cachedSign;

    private long updatedTick = -1;

    public SignUpdaterTileEntity() {
        super(ModBlocks.signUpdaterTileEntity.get());
    }

    public Direction getFacing() {
        return getBlockState().get(BlockStateProperties.FACING);
    }

    public boolean hasSignAttached() {
        assert world != null;
        BlockPos neighbor = pos.offset(this.getFacing());
        return world.getBlockState(neighbor).getBlock() instanceof AbstractSignBlock;
    }

    public SignTileEntity getSignAttached() {
        assert world != null;
        BlockPos neighbor = pos.offset(this.getFacing());
        return (SignTileEntity) Objects.requireNonNull(world.getTileEntity(neighbor));
    }

    @Nullable
    public SignTileEntity getSignAttachedNullable() {
        assert world != null;
        BlockPos neighbor = pos.offset(this.getFacing());
        TileEntity tile = world.getTileEntity(neighbor);
        if (tile instanceof SignTileEntity) {
            return (SignTileEntity) tile;
        }
        return null;
    }

    void invalidateSignCache() {
        cachedSign = null;
    }

    boolean isSignCacheValid() {
        return cachedSign != null;
    }

    @Nullable
    private SignTileEntity getCachedSign() {
        if (!isSignCacheValid()) {
            cachedSign = getSignAttachedNullable();
        }
        return cachedSign;
    }

    @Override
    public int getLines() {
        return 4;
    }

    @Override
    public ITextComponent getLine(int line) {
        SignTileEntity sign = getCachedSign();
        if (sign == null) {
            return EMPTY_TEXT_COMPONENT;
        }
        return sign.getText(line);
    }

    @Override
    public void setLine(int line, ITextComponent text) {
        SignTileEntity sign = getCachedSign();
        if (sign != null) {
            sign.setText(line, text);
            syncData();
        }
    }

    @Override
    public void addLine(ITextComponent line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ITextComponent> view() {
        SignTileEntity sign = getCachedSign();
        if (sign == null) {
            return ImmutableList.of();
        }
        return Arrays.asList(sign.signText);
    }

    @Override
    public Stream<ITextComponent> stream() {
        SignTileEntity sign = getCachedSign();
        if (sign == null) {
            return Stream.empty();
        }
        return Arrays.stream(sign.signText);
    }

    @Override
    public Stream<String> textStream() {
        return stream().map(ITextComponent::getFormattedText);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityDocuments.TEXT_DISPLAY_CAPABILITY) {
            return textDisplayCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        textDisplayCap.invalidate();
        super.invalidateCaps();
    }

    public void syncData() {
        assert world != null;
        if (updatedTick == world.getGameTime()) {
            return;
        }

        BlockPos neighbor = pos.offset(this.getFacing());
        BlockState neighborState = world.getBlockState(neighbor);
        world.notifyBlockUpdate(neighbor, neighborState, neighborState, Constants.BlockFlags.DEFAULT);
        updatedTick = world.getGameTime();
    }
}
