package vswe.stevesfactory.logic.procedure;

import net.minecraft.util.Direction;

import java.util.List;

public interface IDirectionTarget {

    List<Direction> getDirections(int id);

    default boolean isNorthEnabled(int id) {
        return getDirections(id).contains(Direction.NORTH);
    }

    default boolean isSouthEnabled(int id) {
        return getDirections(id).contains(Direction.SOUTH);
    }

    default boolean isUpEnabled(int id) {
        return getDirections(id).contains(Direction.UP);
    }

    default boolean isDownEnabled(int id) {
        return getDirections(id).contains(Direction.DOWN);
    }

    default boolean isWestEnabled(int id) {
        return getDirections(id).contains(Direction.WEST);
    }

    default boolean isEastEnabled(int id) {
        return getDirections(id).contains(Direction.EAST);
    }

    default List<Direction> getDirections() {
        return getDirections(0);
    }
}
