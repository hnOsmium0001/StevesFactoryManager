package vswe.stevesfactory.api.capability;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IRedstoneEventBus {

    boolean hasSignal();

    /**
     * Register an event handler. The event handler will be fired upon any redstone change on any side of of the block.
     *
     * @see #subscribeEvent(Predicate)
     */
    void subscribeEvent(Consumer<SignalStatus> onChange);

    /**
     * Register an event handler. If the lambda return {@code true}, the event handler will be removed. The event handler will be fired upon
     * any redstone change on any side of of the block.
     *
     * @see #subscribeEvent(Consumer)
     */
    void subscribeEvent(Predicate<SignalStatus> onChange);
}
