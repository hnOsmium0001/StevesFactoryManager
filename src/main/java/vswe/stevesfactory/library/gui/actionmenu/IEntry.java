package vswe.stevesfactory.library.gui.actionmenu;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.library.IWidget;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import javax.annotation.Nullable;
import java.awt.*;

import static vswe.stevesfactory.utils.RenderingHelper.fontRenderer;

public interface IEntry extends IWidget, RelocatableWidgetMixin {

    int MARGIN_SIDES = 2;
    int ICON_WIDTH = 16;
    int ICON_HEIGHT = 16;

    /**
     * This icon must have a size of 16*16, and action menus will assume so to function. Failure to do so might create undefined behaviors.
     */
    @Nullable
    ResourceLocation getIcon();

    default String getText() {
        return I18n.format(getTranslationKey());
    }

    String getTranslationKey();

    @Override
    Dimension getDimensions();

    @Override
    default int getWidth() {
        return MARGIN_SIDES + ICON_WIDTH + 4 + fontRenderer().getStringWidth(getText()) + MARGIN_SIDES;
    }

    @Override
    default int getHeight() {
        return MARGIN_SIDES + ICON_HEIGHT + MARGIN_SIDES;
    }
}
