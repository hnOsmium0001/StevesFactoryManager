package vswe.stevesfactory.api.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class SignalReactor implements ISignalReactor {

    private BooleanSupplier signal;
    private List<Predicate<SignalStatus>> eventHandlers = new ArrayList<>();

    public SignalReactor(BooleanSupplier signal) {
        this.signal = signal;
    }

    public SignalReactor() {
        this.signal = () -> false;
    }

    @Override
    public boolean hasSignal() {
        return signal.getAsBoolean();
    }

    @Override
    public void subscribeEvent(Consumer<SignalStatus> onChange) {
        eventHandlers.add(status -> {
            onChange.accept(status);
            return false;
        });
    }

    @Override
    public void subscribeEvent(Predicate<SignalStatus> onChange) {
        eventHandlers.add(onChange);
    }
}
