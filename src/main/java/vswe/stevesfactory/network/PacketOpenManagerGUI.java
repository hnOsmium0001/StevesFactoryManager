package vswe.stevesfactory.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import java.util.Collection;
import java.util.function.Supplier;

public final class PacketOpenManagerGUI {

    public static void openFactoryManager(ServerPlayerEntity client, DimensionType dimension, BlockPos controllerPos, Collection<BlockPos> inventories, Collection<CommandGraph> graphs) {
        NetworkHandler.sendTo(client, new PacketOpenManagerGUI(
                controllerPos,
                new PacketTransferLinkedInventories(controllerPos, inventories),
                new PacketSyncCommandGraphs(graphs, dimension, controllerPos)));
    }

    public static void encode(PacketOpenManagerGUI msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.controllerPos);
        PacketTransferLinkedInventories.encode(msg.inventories, buf);
        PacketSyncCommandGraphs.encode(msg.graphs, buf);
    }

    public static PacketOpenManagerGUI decode(PacketBuffer buf) {
        BlockPos controllerPos = buf.readBlockPos();
        PacketTransferLinkedInventories inventories = PacketTransferLinkedInventories.decode(buf);
        PacketSyncCommandGraphs graphs = PacketSyncCommandGraphs.decode(buf);
        return new PacketOpenManagerGUI(controllerPos, inventories, graphs);
    }

    public static void handle(PacketOpenManagerGUI msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            PacketTransferLinkedInventories.handle(msg.inventories);
            PacketSyncCommandGraphs.handle(msg.graphs);
            Minecraft.getInstance().displayGuiScreen(new FactoryManagerGUI(msg.controllerPos));
            ctx.setPacketHandled(true);
        });
    }

    private BlockPos controllerPos;
    private PacketTransferLinkedInventories inventories;
    private PacketSyncCommandGraphs graphs;

    public PacketOpenManagerGUI(BlockPos controllerPos, PacketTransferLinkedInventories inventories, PacketSyncCommandGraphs graphs) {
        this.controllerPos = controllerPos;
        this.inventories = inventories;
        this.graphs = graphs;
    }
}
