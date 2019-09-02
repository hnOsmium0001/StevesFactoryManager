package vswe.stevesfactory.ui.manager.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.widget.button.TextButton;

import static vswe.stevesfactory.utils.RenderingHelper.getXForHorizontallyCenteredText;
import static vswe.stevesfactory.utils.RenderingHelper.getYForVerticallyCenteredText;

class ActivationButton extends TextButton {

    private DirectionButton target;

    public ActivationButton(DirectionButton target) {
        this.setTarget(target);
        this.setDimensions(42, 12);
    }

    public void setTarget(DirectionButton target) {
        this.target = target;
        updateText();
    }

    @Override
    protected void renderText() {
        // 0.7F, 3, and 3 are magic numbers
        // It would require a lot of effort (including rewriting FontRenderer#getStringWidth) to get rid of those magic numbers
        GlStateManager.enableTexture();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(getAbsoluteX() + 3, getAbsoluteY() + 3, 0F);
        GlStateManager.scalef(0.7F, 0.7F, 1F);
        fontRenderer().drawString(getText(), 0F, 0F, 0xff4d4d4d);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isEnabled()) {
            target.selected = !target.selected;
            updateText();
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    private void updateText() {
        text = target.selected ? I18n.format("gui.sfm.Menu.Deactivate") : I18n.format("gui.sfm.Menu.Activate");
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        if (isEnabled()) {
            super.render(mouseX, mouseY, particleTicks);
        }
    }

    @Override
    public int getNormalBorderColor() {
        return 0xff4b4b4b;
    }

    @Override
    public int getHoveredBorderColor() {
        return 0xff4b4b4b;
    }

    @Override
    public int getNormalBackgroundColor() {
        return 0xffa3a3a3;
    }

    @Override
    public int getHoveredBackgroundColor() {
        return 0xffb4b4b4;
    }
}
