package vswe.stevesfactory.logic.procedure;

import net.minecraft.util.Direction;

import java.util.Set;

public interface IDirectionTarget {

    Set<Direction> getDirections(int id);

    default Set<Direction> getDirections() {
        return getDirections(0);
    }

    default boolean isEnabled(int id, Direction direction) {
        return getDirections(id).contains(direction);
    }

    /**
     * Update the presence of the given direction in the set {@link #getDirections(int)}. Handles dirty marking automatically.
     */
    default void setEnabled(int id, Direction direction, boolean enabled) {
        Set<Direction> d = getDirections(id);
        if (enabled) {
            d.add(direction);
        } else {
            d.remove(direction);
        }
        markDirty();
    }

    /**
     * Mark this data target dirty for updating internal caches. This must be called when data is mutated indirectly,
     * i.e. by mutating any references returned by getters.
     * <p>
     * Implementations are required to handle mark dirty for setters methods, such that users can call them without
     * worrying about marking the data target dirty.
     */
    void markDirty();
}
