/* Code is adapted from McJtyLib
 * https://github.com/McJtyMods/McJtyLib/blob/1.12/src/main/java/mcjty/lib/gui/widgets/TextField.java
 */

package vswe.stevesfactory.library.gui.widget;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.RenderingHelper;

import java.awt.*;

public class TextField extends AbstractWidget implements RelocatableWidgetMixin, ResizableWidgetMixin, LeafWidgetMixin {

    public static TextField DUMMY = new TextField(0, 0, 0, 0) {
        @Override
        public boolean isEditable() {
            return false;
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return false;
        }

        @Override
        public boolean charTyped(char typedChar, int keyCode) {
            return false;
        }

        @Override
        public void setX(int x) {
        }

        @Override
        public void setY(int y) {
        }

        @Override
        public void onParentPositionChanged() {
        }

        @Override
        public boolean isFocused() {
            return false;
        }
    };

    public enum BackgroundStyle implements IBackgroundRenderer {
        NONE {
            @Override
            public void render(int x1, int y1, int x2, int y2, boolean hovered, boolean focused) {
            }
        },
        THICK_BEVELED {
            @Override
            public void render(int x1, int y1, int x2, int y2, boolean hovered, boolean focused) {
                int color = focused ? 0xffeeeeee
                        : hovered ? 0xffdadada
                        : 0xffc6c6c6;
                RenderingHelper.drawThickBeveledBox(x1, y1, x2 - 1, y2 - 1, 1, 0xff2b2b2b, 0xffffffff, color);
            }
        },
        RED_OUTLINE {
            @Override
            public void render(int x1, int y1, int x2, int y2, boolean hovered, boolean focused) {
                if (focused) {
                    RenderingHelper.drawRect(x1, y1, x2, y2, 0xffcf191f);
                    RenderingHelper.drawVerticalGradientRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xff191919, 0xff313131);
                } else {
                    RenderingHelper.drawRect(x1, y1, x2, y2, 0xff6d0b0e);
                    RenderingHelper.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xff1c1c1c);
                }
            }
        }
    }

    public static final int SECONDARY_BUTTON = 1;

    private IBackgroundRenderer backgroundStyle = BackgroundStyle.THICK_BEVELED;

    private String text = "";
    private int cursor = 0;
    /**
     * Index of the first character that will be drawn.
     */
    private int startOffset = 0;
    /**
     * One end of the selected region. If nothing is selected, is should be -1.
     */
    private int selection = -1;
    private boolean editable = true;

    public TextField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public TextField(Point location, Dimension dimensions) {
        super(location, dimensions);
    }

    public boolean isEditable() {
        return editable;
    }

    @CanIgnoreReturnValue
    public TextField setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    public String getText() {
        return text;
    }

    @CanIgnoreReturnValue
    public TextField setText(String text) {
        this.text = text;
        cursor = text.length();
        if (startOffset >= cursor) {
            startOffset = Math.max(cursor - 1, 0);
        }
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isEnabled() && editable) {
            getWindow().setFocusedWidget(this);
            if (button == SECONDARY_BUTTON) {
                setText("");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_C: {
                    copyText();
                    break;
                }
                case GLFW.GLFW_KEY_V: {
                    pasteText();
                    break;
                }
                case GLFW.GLFW_KEY_X: {
                    cutText();
                    break;
                }
                case GLFW.GLFW_KEY_A: {
                    selectAll();
                    break;
                }
            }
        } else {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ESCAPE:
                    getWindow().changeFocus(this, false);
                case GLFW.GLFW_KEY_ENTER:
                case GLFW.GLFW_KEY_DOWN:
                case GLFW.GLFW_KEY_UP:
                case GLFW.GLFW_KEY_TAB: {
                    return false;
                }
                case GLFW.GLFW_KEY_HOME: {
                    updateSelection();
                    cursor = 0;
                    break;
                }
                case GLFW.GLFW_KEY_END: {
                    updateSelection();
                    cursor = text.length();
                    break;
                }
                case GLFW.GLFW_KEY_LEFT: {
                    updateSelection();
                    if (cursor > 0) {
                        cursor--;
                    }
                    break;
                }
                case GLFW.GLFW_KEY_RIGHT: {
                    updateSelection();
                    if (cursor < text.length()) {
                        cursor++;
                    }
                    break;
                }
                case GLFW.GLFW_KEY_BACKSPACE: {
                    if (isRegionSelected()) {
                        replaceSelectedRegion("");
                    } else if (!text.isEmpty() && cursor > 0) {
                        text = text.substring(0, cursor - 1) + text.substring(cursor);
                        cursor--;
                    }
                    break;
                }
                case GLFW.GLFW_KEY_DELETE: {
                    if (isRegionSelected()) {
                        replaceSelectedRegion("");
                    } else if (cursor < text.length()) {
                        text = text.substring(0, cursor) + text.substring(cursor + 1);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        // e.g. F1~12, insert
        // Char code of 0 will appear to be nothing
        if ((int) typedChar != 0) {
            if (isRegionSelected()) {
                replaceSelectedRegion(String.valueOf(typedChar));
            } else {
                text = text.substring(0, cursor) + typedChar + text.substring(cursor);
            }
            cursor++;
            return true;
        }
        return false;
    }

    private void copyText() {
        if (isRegionSelected()) {
            minecraft().keyboardListener.setClipboardString(getSelectedText());
        }
    }

    private void pasteText() {
        String text = minecraft().keyboardListener.getClipboardString();
        if (isRegionSelected()) {
            replaceSelectedRegion(text);
        } else {
            this.text = this.text.substring(0, cursor) + text + this.text.substring(cursor);
        }
        cursor += text.length();
    }

    private void cutText() {
        if (isRegionSelected()) {
            minecraft().keyboardListener.setClipboardString(getSelectedText());
            replaceSelectedRegion("");
        }
    }

    @CanIgnoreReturnValue
    public TextField scrollToFront() {
        cursor = 0;
        startOffset = 0;
        return this;
    }

    @CanIgnoreReturnValue
    public TextField selectAll() {
        return setSelection(0, text.length());
    }

    @CanIgnoreReturnValue
    public TextField setSelection(int start, int end) {
        selection = start;
        cursor = end;
        return this;
    }

    @CanIgnoreReturnValue
    public TextField clearSelection() {
        selection = -1;
        return this;
    }

    public boolean isRegionSelected() {
        return selection != -1;
    }

    /**
     * Inclusive text index indicating start of the selected region. If nothing is selected, it will return -1.
     */
    public int getSelectionStart() {
        return Math.min(cursor, selection);
    }

    /**
     * Exclusive text index indicating end of the selecred region, If nothing is selected, it will return {@link #cursor}.
     */
    public int getSelectionEnd() {
        return Math.max(cursor, selection);
    }

    public String getSelectedText() {
        return text.substring(getSelectionStart(), getSelectionEnd());
    }

    @CanIgnoreReturnValue
    public TextField replaceSelectedRegion(String replacement) {
        int selectionStart = getSelectionStart();
        text = text.substring(0, selectionStart) + replacement + text.substring(getSelectionEnd());
        cursor = selectionStart;
        clearSelection();
        return this;
    }

    private void updateSelection() {
        if (Screen.hasShiftDown()) {
            // Don't clear selection as long as shift is pressed
            if (!isRegionSelected()) {
                selection = cursor;
            }
        } else {
            clearSelection();
        }
    }

    private int calculateVerticalOffset() {
        return (getDimensions().height - fontRenderer().FONT_HEIGHT) / 2;
    }

    private void ensureVisible() {
        if (cursor < startOffset) {
            startOffset = cursor;
        } else {
            int w = fontRenderer().getStringWidth(text.substring(startOffset, cursor));
            while (w > getDimensions().width - 12) {
                startOffset++;
                w = fontRenderer().getStringWidth(text.substring(startOffset, cursor));
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

        ensureVisible();

        int x = getAbsoluteX();
        int y = getAbsoluteY();
        int x2 = getAbsoluteXBR();
        int y2 = getAbsoluteYBR();

        backgroundStyle.render(x, y, x2, y2, isInside(mouseX, mouseY), isFocused());

        String renderedText = fontRenderer().trimStringToWidth(this.text.substring(startOffset), getDimensions().width - 10);
        int textX = x + 5;
        int textY = y + calculateVerticalOffset();
        if (isEnabled()) {
            if (isEditable()) {
                fontRenderer().drawString(renderedText, textX, textY, 0xff000000);
            } else {
                fontRenderer().drawString(renderedText, textX, textY, 0xff333333);
            }

            if (isRegionSelected()) {
                int selectionStart = getSelectionStart();
                int selectionEnd = getSelectionEnd();

                // Text: abcdefghijklmn
                // Rendered: abcdefg, length=7
                //                 ^6
                int renderedStart = MathHelper.clamp(selectionStart - startOffset, 0, renderedText.length());
                int renderedEnd = MathHelper.clamp(selectionEnd - startOffset, 0, renderedText.length());

                String renderedSelection = renderedText.substring(renderedStart, renderedEnd);
                String renderedPreSelection = renderedText.substring(0, renderedStart);
                int selectionX = textX + fontRenderer().getStringWidth(renderedPreSelection);
                int selectionWidth = fontRenderer().getStringWidth(renderedSelection);
                RenderingHelper.drawColorLogic(selectionX - 1, textY, selectionWidth + 1, fontRenderer().FONT_HEIGHT, 60, 147, 242, GlStateManager.LogicOp.OR_REVERSE);
            }
        } else {
            fontRenderer().drawString(renderedText, textX, textY, 0xffa0a0a0);
        }

        if (isFocused()) {
            int w = fontRenderer().getStringWidth(text.substring(startOffset, cursor));
            int cx = x + 5 + w;
            RenderingHelper.drawRect(cx, y + 2, cx + 1, y2 - 3, 0xff000000);
        }

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    public IBackgroundRenderer getBackgroundStyle() {
        return backgroundStyle;
    }

    @CanIgnoreReturnValue
    public TextField setBackgroundStyle(IBackgroundRenderer backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
        return this;
    }

    private String getSelectedTextSafe() {
        if (isRegionSelected()) {
            return getSelectedText();
        }
        return "";
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        super.provideInformation(receiver);
        receiver.line("Editable=" + editable);
        receiver.line("Text=" + text);
        receiver.line("StartOffset=" + startOffset);
        receiver.line("Cursor=" + cursor);
        receiver.line("SelectionStart=" + getSelectionStart());
        receiver.line("SelectionEnd=" + getSelectionEnd());
        receiver.line("SelectedText=" + getSelectedTextSafe());
    }
}
