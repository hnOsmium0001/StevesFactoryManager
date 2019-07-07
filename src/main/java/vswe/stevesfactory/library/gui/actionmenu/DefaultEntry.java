package vswe.stevesfactory.library.gui.actionmenu;

import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nullable;
import java.awt.*;

public class DefaultEntry extends AbstractWidget implements IEntry, LeafWidgetMixin {

    private final ResourceLocation icon;
    private final String translationKey;

    public DefaultEntry(@Nullable ResourceLocation icon, String translationKey) {
        super(-1, -1);
        this.icon = icon;
        this.translationKey = translationKey;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        ResourceLocation icon = getIcon();
        if (icon != null) {
            int iconX = x + MARGIN_SIDES;
            int iconY = y + MARGIN_SIDES;
            RenderingHelper.drawCompleteTexture(iconX, iconY, iconX + ICON_WIDTH, iconY + ICON_HEIGHT, icon);
        }

        int textX = x + MARGIN_SIDES + ICON_WIDTH + 4;
        RenderingHelper.drawTextCenteredVertically(getText(), textX, y, getAbsoluteYBR(), 0xffffffff);
    }

    @Override
    public Dimension getDimensions() {
        Dimension bounds = super.getDimensions();
        if (bounds.width == -1 && bounds.height == -1) {
            bounds.width = IEntry.super.getWidth();
            bounds.height = IEntry.super.getHeight();
        }
        return bounds;
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }
}
