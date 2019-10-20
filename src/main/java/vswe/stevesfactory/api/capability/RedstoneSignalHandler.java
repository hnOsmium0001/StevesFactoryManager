package vswe.stevesfactory.api.capability;

public class RedstoneSignalHandler implements IRedstoneHandler {

    private boolean strong = false;
    private int signal = 0;

    @Override
    public int getSignal() {
        return signal;
    }

    @Override
    public void setSignal(int signal) {
        this.signal = signal;
    }

    @Override
    public boolean isStrong() {
        return strong;
    }

    @Override
    public void setType(Type type) {
        strong = type.isStrong();
    }
}
