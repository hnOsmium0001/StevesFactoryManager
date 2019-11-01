package vswe.stevesfactory.logic.procedure;

import net.minecraft.util.Direction;

import java.util.Set;

public interface IDirectionTarget {

    Set<Direction> getDirections(int id);

    default boolean isEnabled(int id, Direction direction) {
        return getDirections(id).contains(direction);
    }

    default void setEnabled(int id, Direction direction, boolean enabled) {
        Set<Direction> d = getDirections(id);
        if (enabled) {
            d.add(direction);
        } else {
            d.remove(direction);
        }
    }

    default Set<Direction> getDirections() {
        return getDirections(0);
    }
}
