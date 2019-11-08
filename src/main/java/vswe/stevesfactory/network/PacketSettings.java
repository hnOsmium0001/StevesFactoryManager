package vswe.stevesfactory.network;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.library.gui.debug.Inspections;

import java.util.function.Supplier;

public final class PacketSettings {

    public static void query(ServerPlayerEntity client, String name) {
        NetworkHandler.sendTo(client, new PacketSettings(Mode.QUERY, name, false));
    }

    public static void set(ServerPlayerEntity client, String name, boolean value) {
        NetworkHandler.sendTo(client, new PacketSettings(Mode.SET, name, value));
    }

    public enum Mode {
        QUERY, SET;

        public static final Mode[] VALUES = values();
    }

    public static void encode(PacketSettings msg, PacketBuffer buf) {
        buf.writeInt(msg.mode.ordinal());
        buf.writeString(msg.name);
        buf.writeBoolean(msg.value);
    }

    public static PacketSettings decode(PacketBuffer buf) {
        Mode mode = Mode.VALUES[buf.readInt()];
        String name = buf.readString();
        boolean value = buf.readBoolean();
        return new PacketSettings(mode, name, value);
    }

    public static void handle(PacketSettings msg, Supplier<NetworkEvent.Context> ctx) {
        Preconditions.checkState(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleClient(PacketSettings msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            switch (msg.mode) {
                case QUERY:
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent(I18n.format("message.sfm.settings.query", msg.name, Inspections.enabled)));
                    break;
                case SET:
                    Inspections.enabled = msg.value;
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent(I18n.format("message.sfm.settings.set", msg.name, Inspections.enabled)));
                    break;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private Mode mode;
    private String name;
    private boolean value;

    public PacketSettings(Mode mode, String name, boolean value) {
        this.mode = mode;
        this.name = name;
        this.value = value;
    }
}
