package vswe.stevesfactory.api.capability;

public class RedstoneSignalHandler implements IRedstoneHandler {

    private boolean strong = false;
    private int signal = 0;

    @Override
    public int getSignal() {
        return signal;
    }

    @Override
    public boolean isStrong() {
        return strong;
    }
}
