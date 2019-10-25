package vswe.stevesfactory.api.capability;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public class SignalReactor implements ISignalReactor {

    @Override
    public boolean hasSignal() {
        return false;
    }

    @Override
    public void subscribeEvent(BooleanConsumer onChange) {
    }

    @Override
    public void subscribeEvent(Runnable onHigh, Runnable onLow) {
    }
}
