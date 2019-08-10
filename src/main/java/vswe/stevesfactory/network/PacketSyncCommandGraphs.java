package vswe.stevesfactory.network;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.ICommandGraph;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.logic.graph.CommandGraph;

import java.util.*;
import java.util.function.Supplier;

public class PacketSyncCommandGraphs {

    public static void encode(PacketSyncCommandGraphs msg, PacketBuffer buf) {
        buf.writeInt(msg.commandGraphs.size());
        for (ICommandGraph graph : msg.commandGraphs) {
            buf.writeCompoundTag(graph.serialize());
        }
        buf.writeResourceLocation(Objects.requireNonNull(msg.dimension.getRegistryName()));
        buf.writeBlockPos(msg.pos);
    }

    public static PacketSyncCommandGraphs decode(PacketBuffer buf) {
        int size = buf.readInt();
        List<ICommandGraph> graphs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CompoundNBT tag = buf.readCompoundTag();
            // TODO custom implementation compat
            graphs.add(CommandGraph.deserializeFrom(tag));
        }
        DimensionType dimension = DimensionType.byName(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();
        return new PacketSyncCommandGraphs(graphs, dimension, pos);
    }

    public static void handle(PacketSyncCommandGraphs msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            // TODO compat for S to C
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            World world = server.getWorld(msg.dimension);
            TileEntity tile = world.getTileEntity(msg.pos);
            if (tile instanceof INetworkController) {
                INetworkController controller = (INetworkController) tile;
                controller.removeAllCommandGraphs();
                controller.addCommandGraphs(msg.commandGraphs);
            } else {
                StevesFactoryManager.logger.warn("Received packet with invalid controller position! {}", msg);
            }
        });
    }

    private Collection<ICommandGraph> commandGraphs;
    private DimensionType dimension;
    private BlockPos pos;

    public PacketSyncCommandGraphs(Collection<ICommandGraph> commandGraphs, DimensionType dimension, BlockPos pos) {
        this.commandGraphs = commandGraphs;
        this.dimension = dimension;
        this.pos = pos;
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
