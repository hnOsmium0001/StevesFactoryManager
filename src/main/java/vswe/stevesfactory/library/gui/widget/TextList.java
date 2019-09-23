package vswe.stevesfactory.library.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;

import java.util.Collections;
import java.util.List;

import static vswe.stevesfactory.library.gui.RenderingHelper.fontHeight;

public class TextList extends AbstractWidget implements LeafWidgetMixin {

    private List<String> texts;
    private List<String> textView;
    private boolean fitContents = false;

    private int fontHeight = fontHeight();
    private float scaleFactor = (float) fontHeight / fontHeight();

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
        GlStateManager.enableTexture();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 0F);
        GlStateManager.scalef(scaleFactor, scaleFactor, 1F);
        for (String text : texts) {
            fontRenderer().drawString(text, 0, 0, 0x000000);
            GlStateManager.translatef(0F, fontHeight, 0F);
        }
        GlStateManager.popMatrix();
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    public boolean doesFitContents() {
        return fitContents;
    }

    public void setFitContents(boolean fitContents) {
        this.fitContents = fitContents;
    }

    public List<String> getTexts() {
        return textView;
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

    public void addLineSplit(String text) {
        addLineSplit(getWidth(), text);
    }

    public void addLineSplit(int maxWidth, String text) {
        int end = fontRenderer().sizeStringToWidth(text, maxWidth);
        if (end >= text.length()) {
            addLine(text);
        } else {
            String trimmed = text.substring(0, end);
            String after = text.substring(end).trim();
            addLine(trimmed);
            addLineSplit(maxWidth, after);
        }
    }

    public void addTranslatedLineSplit(int maxWidth, String translationKey) {
        addLineSplit(maxWidth, I18n.format(translationKey));
    }

    public void addTranslatedLineSplit(int maxWidth, String translationKey, Object... args) {
        addLineSplit(maxWidth, I18n.format(translationKey, args));
    }

    private void tryExpand(String line) {
        if (fitContents) {
            int w = (int) (minecraft().fontRenderer.getStringWidth(line) * scaleFactor);
            setWidth(Math.max(getWidth(), 1 + w + 1));
            setHeight(1 + (fontHeight + 2) * texts.size() + 1);
        }
    }

    public int getFontHeight() {
        return fontHeight;
    }

    public void setFontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
        this.scaleFactor = (float) fontHeight / fontHeight();
    }
}
