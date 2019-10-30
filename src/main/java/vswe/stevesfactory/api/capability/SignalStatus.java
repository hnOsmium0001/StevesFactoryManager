package vswe.stevesfactory.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public final class SignalStatus {

    public static SignalStatus scan(World world, BlockPos pos) {
        int down = world.getRedstonePower(pos.down(), Direction.DOWN);
        int up = world.getRedstonePower(pos.up(), Direction.UP);
        int north = world.getRedstonePower(pos.north(), Direction.NORTH);
        int south = world.getRedstonePower(pos.south(), Direction.SOUTH);
        int west = world.getRedstonePower(pos.west(), Direction.WEST);
        int east = world.getRedstonePower(pos.east(), Direction.EAST);
        boolean signal = down > 0 || up > 0 || north > 0 || south > 0 || west > 0 || east > 0;
        return new SignalStatus(down, up, north, south, west, east, signal);
    }

    private int down;
    private int up;
    private int north;
    private int south;
    private int west;
    private int east;
    private boolean signal;

    public SignalStatus(int down, int up, int north, int south, int west, int east, boolean signal) {
        this.up = up;
        this.down = down;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.signal = signal;
    }

    public SignalStatus() {
    }

    public int getDown() {
        return down;
    }

    public int getUp() {
        return up;
    }

    public int getNorth() {
        return north;
    }

    public int getSouth() {
        return south;
    }

    public int getWest() {
        return west;
    }

    public int getEast() {
        return east;
    }

    public boolean hasSignal() {
        return signal;
    }

    public int get(Direction direction) {
        switch (direction) {
            case DOWN: return down;
            case UP: return up;
            case NORTH: return north;
            case SOUTH: return south;
            case WEST: return west;
            case EAST: return east;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalStatus that = (SignalStatus) o;
        return down == that.down &&
                up == that.up &&
                north == that.north &&
                south == that.south &&
                west == that.west &&
                east == that.east;
    }

    @Override
    public int hashCode() {
        return Objects.hash(down, up, north, south, west, east);
    }

    public void read(CompoundNBT compound) {
        down = compound.getInt("Down");
        up = compound.getInt("Up");
        north = compound.getInt("North");
        south = compound.getInt("South");
        west = compound.getInt("West");
        east = compound.getInt("East");
        signal = down > 0 || up > 0 || north > 0 || south > 0 || west > 0 || east > 0;
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("Down", down);
        compound.putInt("Up", up);
        compound.putInt("North", north);
        compound.putInt("South", south);
        compound.putInt("West", west);
        compound.putInt("East", east);
        return compound;
    }
}
