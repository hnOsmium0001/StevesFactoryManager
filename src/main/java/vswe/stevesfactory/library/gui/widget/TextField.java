/* Code is adapted from McJtyLib
 * https://github.com/McJtyMods/McJtyLib/blob/1.12/src/main/java/mcjty/lib/gui/widgets/TextField.java
 */

package vswe.stevesfactory.library.gui.widget;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class TextField extends AbstractWidget implements RelocatableWidgetMixin, LeafWidgetMixin {

    public static final int SECONDARY_BUTTON = 1;

    public static Clipboard getAwtClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Nullable
    public static String getClipboardString() {
        try {
            return (String) getAwtClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            StevesFactoryManager.logger.error("Exception when getting clipboard contents", e);
            return null;
        }
    }

    public static void setClipboardString(String text) {
        getAwtClipboard().setContents(new StringSelection(text), null);
    }

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
            startOffset = cursor - 1;
            if (startOffset < 0) {
                startOffset = 0;
            }
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
                case KeyEvent.VK_C: {
                    copyText();
                    break;
                }
                case KeyEvent.VK_V: {
                    pasteText();
                    break;
                }
                case KeyEvent.VK_X: {
                    cutText();
                    break;
                }
                case KeyEvent.VK_A: {
                    selectAll();
                    break;
                }
            }
        } else {
            switch (keyCode) {
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_TAB: {
                    return false;
                }
                case KeyEvent.VK_HOME: {
                    updateSelection();
                    cursor = 0;
                    break;
                }
                case KeyEvent.VK_END: {
                    updateSelection();
                    cursor = text.length();
                    break;
                }
                case KeyEvent.VK_LEFT: {
                    updateSelection();
                    if (cursor > 0) {
                        cursor--;
                    }
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    updateSelection();
                    if (cursor < text.length()) {
                        cursor++;
                    }
                    break;
                }
                case KeyEvent.VK_BACK_SPACE: {
                    if (isRegionSelected()) {
                        replaceSelectedRegion("");
                    } else if (!text.isEmpty() && cursor > 0) {
                        text = text.substring(0, cursor - 1) + text.substring(cursor);
                        cursor--;
                    }
                    break;
                }
                case KeyEvent.VK_DELETE: {
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
            setClipboardString(getSelectedText());
        }
    }

    private void pasteText() {
        String data = getClipboardString();
        if (data == null) {
            return;
        }

        if (isRegionSelected()) {
            replaceSelectedRegion(data);
        } else {
            text = text.substring(0, cursor) + data + text.substring(cursor);
        }
        cursor += data.length();
    }

    private void cutText() {
        if (isRegionSelected()) {
            setClipboardString(getSelectedText());
            replaceSelectedRegion("");
        }
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
        if (Screen.hasShiftDown() && !isRegionSelected()) {
            // Don't clear selection as long as shift is pressed
            selection = cursor;
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
        ensureVisible();

        int color = 0;
        int x = getPosition().x;
        int y = getPosition().y;
        int width = getDimensions().width;
        int height = getDimensions().height;

        RenderingHelper.drawThickBeveledBox(x, y, x + width - 1, y + height - 1, 1, 0xff2b2b2b, 0xffffffff, color);

        String renderedText = fontRenderer().trimStringToWidth(this.text.substring(startOffset), width - 10);
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
            RenderingHelper.drawRect(x + 5 + w, y + 2, x + 5 + w + 1, y + height - 3, 0xff000000);
        }
    }

}
