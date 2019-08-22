package vswe.stevesfactory.library.gui.widget;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;

import static vswe.stevesfactory.utils.RenderingHelper.getRenderer;
import static vswe.stevesfactory.utils.RenderingHelper.rectVertices;

public class Checkbox extends AbstractWidget implements LeafWidgetMixin {

    public static final int NORMAL_BORDER = 0x4d4d4d;
    public static final int UNCHECKED = 0xc3c3c3;
    public static final int CHECKED = 0x5c9e2d;
    public static final int DISABLED_BORDER = 0x8d8d8d;
    public static final int DISABLED_UNCHECKED = 0xd7d6d6;
    public static final int DISABLED_CHECKED = 0x96bf79;

    private boolean checked = false;
    private BooleanConsumer onStateChange = b -> {};

    public Checkbox() {
        this(0, 0, 9, 9);
    }

    public Checkbox(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        int x1 = getAbsoluteX();
        int y1 = getAbsoluteY();
        int x2 = getAbsoluteXRight();
        int y2 = getAbsoluteYBottom();
        int borderColor = isEnabled() ? NORMAL_BORDER : DISABLED_BORDER;
        int contentColor = isEnabled()
                ? (checked ? CHECKED : UNCHECKED)
                : (checked ? DISABLED_CHECKED : DISABLED_UNCHECKED);

        getRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        rectVertices(x1, y1, x2, y2, borderColor);
        rectVertices(x1 + 1, y1 + 1, x2 - 1, y2 - 1, contentColor);
        Tessellator.getInstance().draw();
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        getWindow().setFocusedWidget(this);
        toggle();
        return true;
    }

    public void toggle() {
        checked = !checked;
        onStateChange.accept(checked);
    }

    public boolean isChecked() {
        return checked;
    }

    public BooleanConsumer getActionOnStateChange() {
        return onStateChange;
    }

    public void setActionOnStateChange(BooleanConsumer onStateChange) {
        this.onStateChange = onStateChange;
        onStateChange.accept(checked);
    }
}
