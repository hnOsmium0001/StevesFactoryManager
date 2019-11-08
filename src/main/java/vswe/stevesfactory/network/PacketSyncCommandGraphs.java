package vswe.stevesfactory.network;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.api.network.INetworkController;

import java.util.*;
import java.util.function.Supplier;

public final class PacketSyncCommandGraphs {

    public static void encode(PacketSyncCommandGraphs msg, PacketBuffer buf) {
        buf.writeResourceLocation(Objects.requireNonNull(msg.dimension.getRegistryName()));
        buf.writeBlockPos(msg.pos);

        buf.writeInt(msg.commandGraphs.size());
        for (CommandGraph graph : msg.commandGraphs) {
            buf.writeCompoundTag(graph.serialize());
        }
    }

    public static PacketSyncCommandGraphs decode(PacketBuffer buf) {
        DimensionType dimension = DimensionType.byName(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();

        int size = buf.readInt();
        List<CompoundNBT> graphs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            graphs.add(buf.readCompoundTag());
        }
        return new PacketSyncCommandGraphs(dimension, pos, graphs);
    }

    public static void handle(PacketSyncCommandGraphs msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            Preconditions.checkState(sender != null, "Invalid usage of a client to server packet");

            World world = sender.world;
            TileEntity tile = world.getTileEntity(msg.pos);
            if (tile instanceof INetworkController) {
                INetworkController controller = (INetworkController) tile;
                controller.removeAllCommandGraphs();
                for (CompoundNBT tag : msg.data) {
                    CommandGraph graph = CommandGraph.deserializeFrom(tag, controller);
                    controller.addCommandGraph(graph);
                }
            } else {
                StevesFactoryManager.logger.error("Received packet with invalid controller position {}!", msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private DimensionType dimension;
    private BlockPos pos;

    private List<CompoundNBT> data;
    private Collection<CommandGraph> commandGraphs;

    public PacketSyncCommandGraphs(DimensionType dimension, BlockPos pos, Collection<CommandGraph> commandGraphs) {
        this.dimension = dimension;
        this.pos = pos;
        this.commandGraphs = commandGraphs;
    }

    public PacketSyncCommandGraphs(DimensionType dimension, BlockPos pos, List<CompoundNBT> data) {
        this.dimension = dimension;
        this.pos = pos;
        this.data = data;
    }
}
