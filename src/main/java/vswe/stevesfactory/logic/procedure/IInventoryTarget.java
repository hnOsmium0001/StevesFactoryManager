package vswe.stevesfactory.logic.procedure;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IInventoryTarget {

    List<BlockPos> getInventories(int id);

    default List<BlockPos> getInventories() {
        return getInventories(0);
    }
}
