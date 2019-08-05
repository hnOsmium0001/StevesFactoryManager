package vswe.stevesfactory.library.gui.widget.button;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;

import java.util.function.IntConsumer;

import static vswe.stevesfactory.utils.RenderingHelper.*;

// TODO implement IButton
public class TextButton extends AbstractWidget implements LeafWidgetMixin {

    private static final IntConsumer DUMMY = i -> {
    };

    public static TextButton of(String key) {
        return ofText(I18n.format(key));
    }

    public static TextButton of(String key, IntConsumer action) {
        return ofText(I18n.format(key), action);
    }

    public static TextButton of(String key, Object... args) {
        return ofText(I18n.format(key, args));
    }

    public static TextButton of(String key, IntConsumer action, Object... args) {
        return ofText(I18n.format(key, args), action);
    }

    public static TextButton ofText(String text) {
        TextButton button = new TextButton();
        button.setText(text);
        return button;
    }

    public static TextButton ofText(String text, IntConsumer action) {
        TextButton button = new TextButton();
        button.setText(text);
        button.onClick = action;
        return button;
    }

    private static final int BACKGROUND_COLOR = 0x8c8c8c;
    private static final int NORMAL_BORDER_COLOR = 0x737373;
    private static final int HOVERED_BORDER_COLOR = 0xc9c9c9;

    public IntConsumer onClick = DUMMY;

    private String text;

    public TextButton() {
        super(0, 0, 10, 2 + fontHeight() + 2);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

        usePlainColorGLStates();
        Tessellator.getInstance().getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int x1 = getAbsoluteX();
        int y1 = getAbsoluteY();
        int x2 = getAbsoluteXBR();
        int y2 = getAbsoluteYBR();
        rectVertices(x1, y1, x2, y2, isInside(mouseX, mouseY) ? HOVERED_BORDER_COLOR : NORMAL_BORDER_COLOR);
        rectVertices(x1 + 1, y1 + 1, x2 - 1, y2 - 1, BACKGROUND_COLOR);
        Tessellator.getInstance().draw();

        drawTextCentered(text, y1, y2, x1, x2, 0xffffff);

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        onClick.accept(button);
        return true;
    }

    public void expandToTextWidth() {
        setWidth(Math.max(getWidth(), 4 + fontRenderer().getStringWidth(text) + 4));
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
}
