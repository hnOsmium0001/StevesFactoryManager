package vswe.stevesfactory.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.blocks.FactoryManagerTileEntity;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import java.util.Objects;
import java.util.function.Supplier;

public final class PacketOpenManagerGUI {

    public static void openFactoryManager(ServerPlayerEntity client, DimensionType dimension, BlockPos controllerPos, CompoundNBT tag) {
        NetworkHandler.sendTo(client, new PacketOpenManagerGUI(controllerPos, tag));
    }

    public static void encode(PacketOpenManagerGUI msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.controllerPos);
        buf.writeCompoundTag(msg.tag);
    }

    public static PacketOpenManagerGUI decode(PacketBuffer buf) {
        return new PacketOpenManagerGUI(buf.readBlockPos(), buf.readCompoundTag());
    }

    public static void handle(PacketOpenManagerGUI msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            FactoryManagerTileEntity controller = Objects.requireNonNull((FactoryManagerTileEntity) world.getTileEntity(msg.controllerPos));
            controller.read(msg.tag);

            Minecraft.getInstance().displayGuiScreen(new FactoryManagerGUI(msg.controllerPos));

            ctx.setPacketHandled(true);
        });
    }

    private BlockPos controllerPos;
    private CompoundNBT tag;

    public PacketOpenManagerGUI(BlockPos controllerPos, CompoundNBT tag) {
        this.controllerPos = controllerPos;
        this.tag = tag;
    }
}
