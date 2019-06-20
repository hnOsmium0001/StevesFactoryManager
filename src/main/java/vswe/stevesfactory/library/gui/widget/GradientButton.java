package vswe.stevesfactory.library.gui.widget;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GLAllocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GradientButton extends AbstractWidget implements RelocatableWidgetMixin, ResizableWidgetMixin, LeafWidgetMixin {

    public enum State {
        NORMAL,
        DISABLED,
        CLICKED,
        HOVERING
    }

    public static final int SIDE_MARGIN = 8;

    public static final int TOP_LEFT_COLOR = 0xffeeeeee;
    public static final int BOTTOM_RIGHT_COLOR = 0xff777777;
    public static final int GRADIENT_TOP_COLOR = 0xffb1b1b1;
    public static final int GRADIENT_BOTTOM_COLOR = 0xffe1e1e1;

    public static final int DISABLED_TOP_LEFT_COLOR = 0xffeeeeee;
    public static final int DISABLED_BOTTOM_RIGHT_COLOR = 0xff777777;
    public static final int DISABLED_GRADIENT_TOP_COLOR = 0xffb1b1b1;
    public static final int DISABLED_GRADIENT_BOTTOM_COLOR = 0xffe1e1e1;

    public static final int CLICKED_TOP_LEFT_COLOR = 0xff5c669d;
    public static final int CLICKED_BOTTOM_RIGHT_COLOR = 0xffbcc5ff;
    public static final int CLICKED_GRADIENT_TOP_COLOR = 0xff6a74aa;
    public static final int CLICKED_GRADIENT_BOTTOM_COLOR = 0xff949ed4;

    public static final int HOVERING_TOP_LEFT_COLOR = 0xffa5aac5;
    public static final int HOVERING_BOTTOM_RIGHT_COLOR = 0xff999ebb;
    public static final int HOVERING_GRADIENT_TOP_COLOR = 0xff8d92ad;
    public static final int HOVERING_GRADIENT_BOTTOM_COLOR = 0xffbabfda;

    private static final Map<Pair<Rectangle, State>, Integer> DISPLAY_LISTS = new HashMap<>();

    public static int createOrGetList(int x, int y, int width, int height, State state) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        Pair<Rectangle, State> key = Pair.of(rectangle, state);

        if (DISPLAY_LISTS.containsKey(key)) {
            return DISPLAY_LISTS.get(key);
        }

        int id = GLAllocation.generateDisplayLists(1);
        GlStateManager.newList(id, GL11.GL_COMPILE);
        {
            int colorTopLeft;
            int colorBottomRight;
            int colorGradientTop;
            int colorGradientBottom;
            switch (state) {
                case NORMAL:
                    colorTopLeft = TOP_LEFT_COLOR;
                    colorBottomRight = BOTTOM_RIGHT_COLOR;
                    colorGradientTop = GRADIENT_TOP_COLOR;
                    colorGradientBottom = GRADIENT_BOTTOM_COLOR;
                    break;
                case DISABLED:
                    colorTopLeft = DISABLED_TOP_LEFT_COLOR;
                    colorBottomRight = DISABLED_BOTTOM_RIGHT_COLOR;
                    colorGradientTop = DISABLED_GRADIENT_TOP_COLOR;
                    colorGradientBottom = DISABLED_GRADIENT_BOTTOM_COLOR;
                    break;
                case CLICKED:
                    colorTopLeft = CLICKED_TOP_LEFT_COLOR;
                    colorBottomRight = CLICKED_BOTTOM_RIGHT_COLOR;
                    colorGradientTop = CLICKED_GRADIENT_TOP_COLOR;
                    colorGradientBottom = CLICKED_GRADIENT_BOTTOM_COLOR;
                    break;
                case HOVERING:
                    colorTopLeft = HOVERING_TOP_LEFT_COLOR;
                    colorBottomRight = HOVERING_BOTTOM_RIGHT_COLOR;
                    colorGradientTop = HOVERING_GRADIENT_TOP_COLOR;
                    colorGradientBottom = HOVERING_GRADIENT_BOTTOM_COLOR;
                    break;
                default:
                    throw new IllegalArgumentException("Imaginary state enum object " + state);
            }

            int x2 = x + width;
            int y2 = y + height;
            RenderingHelper.drawRect(x, y, x2, y2, colorBottomRight);
            RenderingHelper.drawRect(x, y, x2 - 1, y2 - 2, colorTopLeft);
            RenderingHelper.drawVerticalGradientRect(x + 1, y + 1, x2 - 1, y2 - 1, colorGradientTop, colorGradientBottom);
        }
        GlStateManager.endList();
        DISPLAY_LISTS.put(key, id);
        return id;
    }

    private String text;
    private State state = State.NORMAL;
    private int bodyDL;

    public GradientButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.text = text;
    }

    public GradientButton(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    public String getText() {
        return text;
    }

    @CanIgnoreReturnValue
    public GradientButton setText(String text) {
        this.text = text;
        return this;
    }

    @CanIgnoreReturnValue
    public GradientButton fitTextWidth() {
        return fitTextWidth(SIDE_MARGIN);
    }

    @CanIgnoreReturnValue
    public GradientButton fitTextWidth(int sideMargin) {
        int textWidth = getFontRenderer().getStringWidth(text);
        setWidth(textWidth + sideMargin * 2);
        return this;
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateToRestingState();
    }

    private void updateToRestingState() {
        if (isEnabled()) {
            setState(State.NORMAL);
        } else {
            setState(State.DISABLED);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isEnabled()) {
            return false;
        }
        setState(State.CLICKED);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isEnabled()) {
            return false;
        }
        updateToRestingState();
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        if (isInside(mouseX, mouseY) && getState() == State.NORMAL) {
            setState(State.HOVERING);
        } else if (getState() == State.HOVERING) {
            updateToRestingState();
        }
        GlStateManager.callList(bodyDL);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        bodyDL = createOrGetList(getAbsoluteX(), getAbsoluteY(), getAbsoluteXBR(), getAbsoluteYBR(), state);
    }

}
