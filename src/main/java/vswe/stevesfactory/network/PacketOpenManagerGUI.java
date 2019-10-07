package vswe.stevesfactory.network;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
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

    public static void handle(PacketOpenManagerGUI msg, Supplier<NetworkEvent.Context> ctx) {
        Preconditions.checkState(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketOpenManagerGUI msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            FactoryManagerTileEntity controller = Objects.requireNonNull((FactoryManagerTileEntity) world.getTileEntity(msg.controllerPos));
            controller.read(msg.tag);

            Minecraft.getInstance().displayGuiScreen(new FactoryManagerGUI(msg.controllerPos));

            ctx.get().setPacketHandled(true);
        });
    }

    private BlockPos controllerPos;
    private CompoundNBT tag;

    public PacketOpenManagerGUI(BlockPos controllerPos, CompoundNBT tag) {
        this.controllerPos = controllerPos;
        this.tag = tag;
    }
}
