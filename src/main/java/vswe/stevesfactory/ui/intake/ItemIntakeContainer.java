package vswe.stevesfactory.ui.intake;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;
import vswe.stevesfactory.blocks.ItemIntakeTileEntity;
import vswe.stevesfactory.library.gui.screen.WidgetContainer;
import vswe.stevesfactory.setup.ModContainers;

import java.util.Objects;

public class ItemIntakeContainer extends WidgetContainer {

    public static void openGUI(ServerPlayerEntity client, ItemIntakeTileEntity tile) {
        NetworkHooks.openGui(client, tile, buf -> {
            buf.writeBlockPos(tile.getPos());
            buf.writeCompoundTag(tile.writeCustom(new CompoundNBT()));
        });
    }

    ItemIntakeTileEntity intake;

    public ItemIntakeContainer(int windowId, ItemIntakeTileEntity intake) {
        super(ModContainers.itemIntakeContainer, windowId);
        this.intake = intake;
    }

    public ItemIntakeContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        super(ModContainers.itemIntakeContainer, windowId);
        BlockPos pos = data.readBlockPos();
        intake = (ItemIntakeTileEntity) Objects.requireNonNull(inv.player.world.getTileEntity(pos));
        intake.readCustom(Objects.requireNonNull(data.readCompoundTag()));
    }
}
