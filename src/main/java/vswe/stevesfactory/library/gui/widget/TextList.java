package vswe.stevesfactory.library.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import java.util.Collections;
import java.util.List;

import static vswe.stevesfactory.utils.RenderingHelper.*;

public class TextList extends AbstractWidget implements LeafWidgetMixin {

    private List<String> texts;
    private List<String> textView;
    private boolean fitContents = false;

    public HorizontalAlignment textAlignment = HorizontalAlignment.LEFT;

    public TextList(int width, int height, List<String> texts) {
        super(0, 0, width, height);
        this.texts = texts;
        this.textView = Collections.unmodifiableList(texts);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        int x = getAbsoluteX() + 1;
        int y = getAbsoluteY() + 1;
        int x2 = getAbsoluteXRight() - 1;
        GlStateManager.enableTexture();
        for (String text : texts) {
            switch (textAlignment) {
                case LEFT:
                    fontRenderer().drawString(text, x, y, 0x000000);
                    break;
                case CENTER:
                    drawTextCenteredHorizontally(text, x, x2, y, 0x000000);
                    break;
                case RIGHT:
                    fontRenderer().drawString(text, RenderingHelper.getXForAlignedRight(x2, textWidth(text)), y, 0x000000);
                    break;
            }
            y += fontHeight();
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    public boolean doesFitContents() {
        return fitContents;
    }

    public void setFitContents(boolean fitContents) {
        this.fitContents = fitContents;
    }

    public void editLine(int line, String newText) {
        texts.set(line, newText);
        tryExpand(newText);
    }

    public void translateLine(int line, String translationKey) {
        editLine(line, I18n.format(translationKey));
    }

    public void translateLine(int line, String translationKey, Object... args) {
        editLine(line, I18n.format(translationKey, args));
    }

    public String getLine(int line) {
        return texts.get(line);
    }

    public void addLine(String newLine) {
        texts.add(newLine);
        tryExpand(newLine);
    }

    public void addTranslatedLine(String translationKey) {
        addLine(I18n.format(translationKey));
    }

    public void addTranslatedLine(String translationKey, Object... args) {
        addLine(I18n.format(translationKey, args));
    }

    public List<String> getTexts() {
        return textView;
    }

    private void tryExpand(String line) {
        if (fitContents) {
            int w = minecraft().fontRenderer.getStringWidth(line);
            setWidth(Math.max(getWidth(), 1 + w + 1));
            setHeight(1 + (fontHeight() + 2) * texts.size() + 1);
        }
    }
}
