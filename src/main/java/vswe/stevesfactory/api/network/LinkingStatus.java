package vswe.stevesfactory.api.network;

import com.google.common.collect.AbstractIterator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.api.network.IConnectable.LinkType;

import java.util.Iterator;

public final class LinkingStatus implements Iterable<Pair<Direction, LinkType>> {

    private static final String KEY_CENTER_POS = "CenterPos";
    private static final String KEY_DOWN = "Down";
    private static final String KEY_UP = "Up";
    private static final String KEY_NORTH = "North";
    private static final String KEY_SOUTH = "South";
    private static final String KEY_WEST = "West";
    private static final String KEY_EAST = "East";

    public static LinkingStatus readFrom(CompoundNBT compound) {
        BlockPos center = NBTUtil.readBlockPos(compound.getCompound(KEY_CENTER_POS));
        LinkingStatus status = new LinkingStatus(center);
        status.down = LinkType.fromID(compound.getInt(KEY_DOWN));
        status.up = LinkType.fromID(compound.getInt(KEY_UP));
        status.north = LinkType.fromID(compound.getInt(KEY_NORTH));
        status.south = LinkType.fromID(compound.getInt(KEY_SOUTH));
        status.west = LinkType.fromID(compound.getInt(KEY_WEST));
        status.east = LinkType.fromID(compound.getInt(KEY_EAST));
        return status;
    }

    private BlockPos center;
    public LinkType up, down, north, south, east, west;

    public LinkingStatus(BlockPos center) {
        this.center = center;
        this.up = LinkType.NEVER;
        this.down = LinkType.NEVER;
        this.north = LinkType.NEVER;
        this.south = LinkType.NEVER;
        this.east = LinkType.NEVER;
        this.west = LinkType.NEVER;
    }

    public BlockPos getCenter() {
        return center;
    }

    public LinkType get(Direction direction) {
        switch (direction) {
            case DOWN:
                return down;
            case UP:
                return up;
            case NORTH:
                return north;
            case SOUTH:
                return south;
            case WEST:
                return west;
            case EAST:
                return east;
        }
        throw new IllegalArgumentException("Nonexistent direction " + direction);
    }

    public void set(Direction direction, LinkType type) {
        switch (direction) {
            case DOWN:
                down = type;
                break;
            case UP:
                up = type;
                break;
            case NORTH:
                north = type;
                break;
            case SOUTH:
                south = type;
                break;
            case WEST:
                west = type;
                break;
            case EAST:
                east = type;
                break;
        }
    }

    /**
     * Use {@link #connections()} when not using for-each loop.
     */
    @Override
    public Iterator<Pair<Direction, LinkType>> iterator() {
        return connections();
    }

    public Iterator<Pair<Direction, LinkType>> connections() {
        return new AbstractIterator<Pair<Direction, LinkType>>() {
            private int last = 0;

            @Override
            protected Pair<Direction, LinkType> computeNext() {
                switch (last++) {
                    case 0:
                        return Pair.of(Direction.DOWN, down);
                    case 1:
                        return Pair.of(Direction.UP, up);
                    case 2:
                        return Pair.of(Direction.NORTH, north);
                    case 3:
                        return Pair.of(Direction.SOUTH, south);
                    case 4:
                        return Pair.of(Direction.WEST, west);
                    case 5:
                        return Pair.of(Direction.EAST, east);
                    default:
                        return endOfData();
                }
            }
        };
    }

    public Iterator<Pair<Direction, LinkType>> connections(LinkType filter) {
        return new AbstractIterator<Pair<Direction, LinkType>>() {
            private final Iterator<Pair<Direction, LinkType>> all = connections();

            @Override
            protected Pair<Direction, LinkType> computeNext() {
                while (all.hasNext()) {
                    Pair<Direction, LinkType> current = all.next();
                    if (current.getRight() == filter) {
                        return current;
                    }
                }
                return endOfData();
            }
        };
    }

    public CompoundNBT write() {
        return write(new CompoundNBT());
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.put(KEY_CENTER_POS, NBTUtil.writeBlockPos(center));
        compound.putInt(KEY_DOWN, down.getID());
        compound.putInt(KEY_UP, up.getID());
        compound.putInt(KEY_NORTH, north.getID());
        compound.putInt(KEY_SOUTH, south.getID());
        compound.putInt(KEY_WEST, west.getID());
        compound.putInt(KEY_EAST, east.getID());
        return compound;
    }

    public void read(CompoundNBT compound) {
        center = NBTUtil.readBlockPos(compound.getCompound(KEY_CENTER_POS));
        down = LinkType.fromID(compound.getInt(KEY_DOWN));
        up = LinkType.fromID(compound.getInt(KEY_UP));
        north = LinkType.fromID(compound.getInt(KEY_NORTH));
        south = LinkType.fromID(compound.getInt(KEY_SOUTH));
        west = LinkType.fromID(compound.getInt(KEY_WEST));
        east = LinkType.fromID(compound.getInt(KEY_EAST));
    }

}
