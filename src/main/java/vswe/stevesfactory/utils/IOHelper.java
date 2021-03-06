package vswe.stevesfactory.utils;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.logic.item.IItemFilter;
import vswe.stevesfactory.logic.item.IItemFilter.ItemFilters;

import java.util.*;
import java.util.function.Function;
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

    public static <T extends IInventory> T readInventory(ListNBT serializedStacks, T target) {
        Preconditions.checkArgument(serializedStacks.size() == target.getSizeInventory());
        for (int i = 0; i < serializedStacks.size(); i++) {
            CompoundNBT tag = serializedStacks.getCompound(i);
            target.setInventorySlotContents(i, ItemStack.read(tag));
        }
        return target;
    }

    public static <T extends Collection<ItemStack>> T readItemStacks(ListNBT serializedFilters, T target) {
        for (int i = 0; i < serializedFilters.size(); i++) {
            target.add(ItemStack.read(serializedFilters.getCompound(i)));
        }
        return target;
    }

    public static ListNBT writeInventory(IInventory inventory) {
        return writeInventory(inventory, new ListNBT());
    }

    public static ListNBT writeInventory(IInventory inventory, ListNBT target) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            target.add(inventory.getStackInSlot(i).write(new CompoundNBT()));
        }
        return target;
    }

    public static ListNBT writeItemStacks(Collection<ItemStack> stacks) {
        return writeItemStacks(stacks, new ListNBT());
    }

    public static ListNBT writeItemStacks(Collection<ItemStack> filters, ListNBT target) {
        for (ItemStack stack : filters) {
            target.add(stack.write(new CompoundNBT()));
        }
        return target;
    }

    public static <T extends Collection<Tag<Item>>> T readItemTags(ListNBT serializedFilters, T target) {
        for (int i = 0; i < serializedFilters.size(); i++) {
            ResourceLocation id = new ResourceLocation(serializedFilters.getString(i));
            Tag<Item> tag = new ItemTags.Wrapper(id);
            target.add(tag);
        }
        return target;
    }

    public static <T> ListNBT writeTags(Collection<Tag<T>> tags, ListNBT target) {
        for (Tag<?> tag : tags) {
            target.add(new StringNBT(tag.getId().toString()));
        }
        return target;
    }

    public static <T> ListNBT writeTags(Collection<Tag<T>> tags) {
        return writeTags(tags, new ListNBT());
    }

    public static CompoundNBT writeItemFilter(IItemFilter filter) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("TypeID", filter.getTypeID());
        filter.write(tag);
        return tag;
    }

    public static IItemFilter readItemFilter(CompoundNBT tag) {
        int typeID = tag.getInt("TypeID");
        Function<CompoundNBT, IItemFilter> deserializer = ItemFilters.getDeserializerFor(typeID);
        Preconditions.checkArgument(deserializer != null);
        return deserializer.apply(tag);
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

    public static <T extends Collection<Direction>> T index2Direction(int[] indices, IntFunction<T> factory) {
        T collection = factory.apply(indices.length);
        for (int index : indices) {
            collection.add(Direction.byIndex(index));
        }
        return collection;
    }

    public static <T extends Collection<Direction>> T index2DirectionFill(int[] indices, T collection) {
        for (int index : indices) {
            collection.add(Direction.byIndex(index));
        }
        return collection;
    }

    public static ListNBT writeStrings(Collection<String> texts) {
        ListNBT list = new ListNBT();
        for (String text : texts) {
            list.add(new StringNBT(text));
        }
        return list;
    }

    public static ListNBT writeStrings(String[] texts) {
        ListNBT list = new ListNBT();
        for (String text : texts) {
            list.add(new StringNBT(text));
        }
        return list;
    }

    public static <T extends Collection<String>> T readStrings(ListNBT list, T target) {
        for (int i = 0; i < list.size(); i++) {
            target.add(list.getString(i));
        }
        return target;
    }

    public static String[] readStrings(ListNBT list) {
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.getString(i);
        }
        return result;
    }

    public static String[] readStrings(ListNBT list, String[] target) {
        Preconditions.checkArgument(list.size() == target.length);
        for (int i = 0; i < list.size(); i++) {
            target[i] = list.getString(i);
        }
        return target;
    }
}
