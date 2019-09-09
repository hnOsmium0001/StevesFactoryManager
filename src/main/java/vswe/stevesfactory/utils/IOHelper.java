package vswe.stevesfactory.utils;

import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.logic.item.SingleItemFilter;

import java.util.*;
import java.util.function.IntFunction;

public final class IOHelper {

    private IOHelper() {
    }

    public static ListNBT writeBlockPoses(Collection<BlockPos> poses) {
        return writeBlockPoses(poses, new ListNBT());
    }

    public static ListNBT writeBlockPoses(Collection<BlockPos> poses, ListNBT target) {
        for (BlockPos pos : poses) {
            target.add(NBTUtil.writeBlockPos(pos));
        }
        return target;
    }

    public static <T extends Collection<BlockPos>> T readBlockPoses(ListNBT serializedPoses, T target) {
        for (int i = 0; i < serializedPoses.size(); i++) {
            target.add(NBTUtil.readBlockPos(serializedPoses.getCompound(i)));
        }
        return target;
    }

    public static void writeBlockPoses(Collection<BlockPos> poses, PacketBuffer buf) {
        buf.writeInt(poses.size());
        for (BlockPos linkable : poses) {
            buf.writeBlockPos(linkable);
        }
    }

    public static SingleItemFilter deserializeItemFiler(CompoundNBT tag) {
        SingleItemFilter filter = new SingleItemFilter();
        filter.read(tag);
        return filter;
    }

    public static <T extends Collection<SingleItemFilter>> T readItemFilters(ListNBT serializedFilters, T target) {
        for (int i = 0; i < serializedFilters.size(); i++) {
            target.add(deserializeItemFiler(serializedFilters.getCompound(i)));
        }
        return target;
    }

    public static ListNBT writeItemFilters(Collection<SingleItemFilter> filters) {
        return writeItemFilters(filters, new ListNBT());
    }

    public static ListNBT writeItemFilters(Collection<SingleItemFilter> filters, ListNBT target) {
        for (SingleItemFilter filter : filters) {
            CompoundNBT tag = new CompoundNBT();
            filter.write(tag);
            target.add(tag);
        }
        return target;
    }

    public static <T extends Collection<BlockPos>> T readBlockPoses(PacketBuffer buf, T target) {
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            target.add(buf.readBlockPos());
        }
        return target;
    }

    public static <T extends Collection<BlockPos>> T readBlockPosesSized(PacketBuffer buf, IntFunction<T> targetFactory) {
        int size = buf.readInt();
        T target = targetFactory.apply(size);
        for (int i = 0; i < size; i++) {
            target.add(buf.readBlockPos());
        }
        return target;
    }

    public static int[] direction2Index(Collection<Direction> directions) {
        int[] res = new int[directions.size()];
        int i = 0;
        for (Direction direction : directions) {
            res[i] = direction.getIndex();
            i++;
        }
        return res;
    }

    public static List<Direction> index2Direction(int[] indices) {
        List<Direction> res = new ArrayList<>(indices.length);
        for (int index : indices) {
            res.add(Direction.byIndex(index));
        }
        return res;
    }
}
