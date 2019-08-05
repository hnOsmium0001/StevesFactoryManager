package vswe.stevesfactory.library.gui.widget;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;

import static vswe.stevesfactory.utils.RenderingHelper.fontHeight;

// WIP
public class Paragraph extends AbstractWidget implements LeafWidgetMixin {

    public static Paragraph of() {
        return of("", 0xffffff);
    }

    public static Paragraph of(int color) {
        return of("", color);
    }

    public static Paragraph of(String text) {
        return of(text, 0xffffff);
    }

    public static Paragraph of(String text, int color) {
        return new Paragraph(text, color);
    }

    private String text;
    private int color;

    public Paragraph(String text, int color) {
        setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setDimensions(fontRenderer().getStringWidth(text), fontHeight());
    }

    public void translate(String translationKey) {
        setText(I18n.format(translationKey));
    }

    public void translate(String translationKey, Object... args) {
        setText(I18n.format(translationKey, args));
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        fontRenderer().drawString(text, getAbsoluteX(), getAbsoluteY(), color);
    }

    // TODO interaction support
    // TODO wrapping support
}
