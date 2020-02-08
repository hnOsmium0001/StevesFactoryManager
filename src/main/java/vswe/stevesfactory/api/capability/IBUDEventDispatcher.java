package vswe.stevesfactory.api.capability;

import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IBUDEventDispatcher {

    void subscribe(Consumer<BlockPos> handler);

    void subscribe(Predicate<BlockPos> handler);
}
