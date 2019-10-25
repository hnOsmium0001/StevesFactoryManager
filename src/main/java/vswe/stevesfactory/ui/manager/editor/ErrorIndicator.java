package vswe.stevesfactory.ui.manager.editor;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IErrorPopulator;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;

import java.util.ArrayList;
import java.util.List;

public class ErrorIndicator extends AbstractWidget implements LeafWidgetMixin {

    public static ErrorIndicator error() {
        return new ErrorIndicator(I18n.format("error.sfm.Error"), ERROR, ERROR_HOVERED);
    }

    public static ErrorIndicator warning() {
        return new ErrorIndicator(I18n.format("error.sfm.Warning"), WARNING, WARNING_HOVERED);
    }

    public static final TextureWrapper ERROR = TextureWrapper.ofFlowComponent(40, 52, 2, 10);
    public static final TextureWrapper ERROR_HOVERED = ERROR.toRight(2);
    public static final TextureWrapper WARNING = ERROR.toRight(1);
    public static final TextureWrapper WARNING_HOVERED = WARNING.toRight(2);

    private TextureWrapper background;
    private TextureWrapper backgroundHovered;
    private final List<String> errors = new ArrayList<>();
    private final String heading;

    private ErrorIndicator(String heading, TextureWrapper background, TextureWrapper backgroundHovered) {
        this.heading = heading;
        this.background = background;
        this.backgroundHovered = backgroundHovered;
        this.setDimensions(background.getPortionWidth(), background.getPortionHeight());
    }

    public void clearErrors() {
        errors.clear();
        errors.add(heading);
    }

    public void populateErrors(IErrorPopulator handler) {
        handler.populateErrors(errors);
    }

    public void repopulateErrors(IErrorPopulator handler) {
        clearErrors();
        handler.populateErrors(errors);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        // We will always have a heading in the list
        if (errors.size() > 1) {
            if (isInside(mouseX, mouseY)) {
                backgroundHovered.draw(getAbsoluteX(), getAbsoluteY());
                WidgetScreen.getCurrentScreen().setHoveringText(errors, mouseX, mouseY);
            } else {
                background.draw(getAbsoluteX(), getAbsoluteY());
            }
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }
}
