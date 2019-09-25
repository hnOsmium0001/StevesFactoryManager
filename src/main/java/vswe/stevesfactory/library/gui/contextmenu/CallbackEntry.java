package vswe.stevesfactory.library.gui.contextmenu;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

public class CallbackEntry extends DefaultEntry {

    private final IntConsumer callback;

    public CallbackEntry(@Nullable ResourceLocation icon, String translationKey, IntConsumer callback) {
        super(icon, translationKey);
        this.callback = callback;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        callback.accept(button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
