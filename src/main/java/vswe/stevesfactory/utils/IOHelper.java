package vswe.stevesfactory.utils;

import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

import java.util.*;

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

    public static ArrayList<BlockPos> readBlockPosesArrayList(ListNBT serializedPoses) {
        return readBlockPoses(serializedPoses, new ArrayList<>());
    }

    public static HashSet<BlockPos> readBlockPosesHashSet(ListNBT serializedPoses) {
        return readBlockPoses(serializedPoses, new HashSet<>());
    }

    public static <T extends Collection<BlockPos>> T readBlockPoses(ListNBT serializedPoses, T target) {
        for (int i = 0; i < serializedPoses.size(); i++) {
            target.add(NBTUtil.readBlockPos(serializedPoses.getCompound(i)));
        }
        return target;
    }

}
