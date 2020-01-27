package vswe.stevesfactory;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

public final class ClientEventHandler {

    private ClientEventHandler() {
    }

    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (Config.CLIENT.showComponentGroupsMessage.get()) {
            ITextComponent msg = new TranslationTextComponent("message.sfm.login.reloadComponentGroupsOnUpdate");
            event.getPlayer().sendStatusMessage(msg, false);
        }
    }
}
