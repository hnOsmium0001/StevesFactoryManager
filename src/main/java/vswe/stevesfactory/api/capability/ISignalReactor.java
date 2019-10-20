package vswe.stevesfactory.api.capability;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface ISignalReactor {

    boolean hasSignal();

    void subscribeEvent(BooleanConsumer onChange);

    void subscribeEvent(Runnable onHigh, Runnable onLow);
}
