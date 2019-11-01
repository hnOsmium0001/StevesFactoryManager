package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

public class RedstoneSignalHandler implements IRedstoneHandler {

    private Runnable onSignalChanged = () -> {};
    private boolean strong = false;
    private int signal = 0;

    public RedstoneSignalHandler(Runnable onSignalChanged) {
        this.onSignalChanged = onSignalChanged;
    }

    public RedstoneSignalHandler() {
    }

    public void onSignalChanged(Runnable onSignalChanged) {
        this.onSignalChanged = onSignalChanged;
    }

    @Override
    public int getSignal() {
        return signal;
    }

    @Override
    public void setSignal(int signal) {
        this.signal = MathHelper.clamp(signal, 0, 15);
        onSignalChanged.run();
    }

    @Override
    public boolean isStrong() {
        return strong;
    }

    @Override
    public boolean isWeak() {
        return !strong;
    }

    @Override
    public void setType(Type type) {
        strong = type.isStrong();
        onSignalChanged.run();
    }

    public void read(CompoundNBT compound) {
        strong = compound.getBoolean("Strong");
        signal = compound.getInt("Signal");
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("Strong", strong);
        compound.putInt("Signal", signal);
        return compound;
    }

    public CompoundNBT write() {
        return write(new CompoundNBT());
    }
}
