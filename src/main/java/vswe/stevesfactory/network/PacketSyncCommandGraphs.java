package vswe.stevesfactory.network;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.utils.Utils;

import java.util.*;
import java.util.function.Supplier;

public class PacketSyncCommandGraphs {

    public static void encode(PacketSyncCommandGraphs msg, PacketBuffer buf) {
        buf.writeInt(msg.commandGraphs.size());
        for (CommandGraph graph : msg.commandGraphs) {
            buf.writeCompoundTag(graph.serialize());
        }
        buf.writeResourceLocation(Objects.requireNonNull(msg.dimension.getRegistryName()));
        buf.writeBlockPos(msg.pos);
    }

    public static PacketSyncCommandGraphs decode(PacketBuffer buf) {
        int size = buf.readInt();
        List<CompoundNBT> graphs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            graphs.add(buf.readCompoundTag());
        }

        DimensionType dimension = DimensionType.byName(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();
        return new PacketSyncCommandGraphs(graphs, dimension, pos);
    }

    public static void handle(PacketSyncCommandGraphs msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            World world = Utils.getWorldForSide(msg.dimension);

            TileEntity tile = world.getTileEntity(msg.pos);
            if (tile instanceof INetworkController) {
                INetworkController controller = (INetworkController) tile;
                Collection<CommandGraph> graphs = msg.getCommandGraphs();
                controller.removeAllCommandGraphs();
                controller.addCommandGraphs(graphs);
            } else {
                StevesFactoryManager.logger.warn("Received packet with invalid controller position! {}", msg);
            }
        });
    }

    private List<CompoundNBT> data;
    private Collection<CommandGraph> commandGraphs;

    private DimensionType dimension;
    private BlockPos pos;

    public PacketSyncCommandGraphs(Collection<CommandGraph> commandGraphs, DimensionType dimension, BlockPos pos) {
        this.commandGraphs = commandGraphs;
        this.dimension = dimension;
        this.pos = pos;
    }

    public PacketSyncCommandGraphs(List<CompoundNBT> data, DimensionType dimension, BlockPos pos) {
        this.data = data;
        this.dimension = dimension;
        this.pos = pos;
    }

    public Collection<CommandGraph> getCommandGraphs() {
        if (commandGraphs == null) {
            commandGraphs = new ArrayList<>();
            for (CompoundNBT tag : data) {
                // TODO custom implementation compat
                commandGraphs.add(CommandGraph.deserializeFrom(tag));
            }
        }
        return commandGraphs;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("commandGraphs", commandGraphs)
                .add("dimension", dimension)
                .add("pos", pos)
                .toString();
    }
}
