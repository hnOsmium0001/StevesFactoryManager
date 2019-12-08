package vswe.stevesfactory.network;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.ProcedureGraph;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.blocks.FactoryManagerTileEntity;

import java.util.Objects;
import java.util.function.Supplier;

public final class PacketSyncFlowcharts {

    public static void encode(PacketSyncFlowcharts msg, PacketBuffer buf) {
        buf.writeResourceLocation(Objects.requireNonNull(msg.dimension.getRegistryName()));
        buf.writeBlockPos(msg.pos);
        buf.writeCompoundTag(msg.graph.serialize());
    }

    public static PacketSyncFlowcharts decode(PacketBuffer buf) {
        DimensionType dimension = DimensionType.byName(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();
        ProcedureGraph graph = ProcedureGraph.create();
        graph.deserialize(Objects.requireNonNull(buf.readCompoundTag()));
        return new PacketSyncFlowcharts(dimension, pos, graph);
    }

    public static void handle(PacketSyncFlowcharts msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            Preconditions.checkState(sender != null, "Invalid usage of a client to server packet");

            World world = sender.world;
            TileEntity tile = world.getTileEntity(msg.pos);
            if (tile instanceof FactoryManagerTileEntity) {
                ((FactoryManagerTileEntity) tile).setPGraph(msg.graph);
            } else {
                StevesFactoryManager.logger.error("Received packet with invalid controller position {}!", msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private DimensionType dimension;
    private BlockPos pos;

    private ProcedureGraph graph;

    public PacketSyncFlowcharts(DimensionType dimension, BlockPos pos, ProcedureGraph graph) {
        this.dimension = dimension;
        this.pos = pos;
        this.graph = graph;
    }
}
