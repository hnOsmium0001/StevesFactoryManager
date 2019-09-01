package vswe.stevesfactory.ui.manager.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.INamedElement;
import vswe.stevesfactory.library.gui.widget.button.IButton;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.utils.RenderingHelper;

public class BlockIcon extends AbstractWidget implements IButton, INamedElement, LeafWidgetMixin {

    private boolean hovered = false;
    private boolean clicked = false;
    private boolean selected = false;

    private BlockState state;
    private ItemStack cachedItemStack;

    public BlockIcon() {
        this(16);
    }

    public BlockIcon(int size) {
        super(0, 0, size, size);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        clicked = true;
        selected = !selected;
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        clicked = false;
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        hovered = isInside(mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return hovered;
    }

    @Override
    public boolean isClicked() {
        return clicked;
    }

    public boolean isSelected() {
        return selected;
    }

    public BlockState getBlockState() {
        return state;
    }

    public void setBlockState(BlockState state) {
        this.state = state;
        this.cachedItemStack = new ItemStack(state.getBlock());
    }

    public ItemStack getCachedItemStack() {
        return cachedItemStack;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

        int x = getAbsoluteX();
        int y = getAbsoluteY();

        int color = selected
                ? (hovered ? 0xff62c93a : 0xffadcfa0)
                : (hovered ? 0xffd4d4d4 : 0xffa4a4a4);
        RenderingHelper.drawRect(x, y, getAbsoluteXRight(), getAbsoluteYBottom(), color);

        // 16 is the standard item size
        GlStateManager.enableTexture();
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(cachedItemStack, x + (getWidth() - 16) / 2, y + (getHeight() - 16) / 2);

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public String getName() {
        return I18n.format(state.getBlock().getTranslationKey());
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        super.provideInformation(receiver);
        receiver.line("BlockState=" + state);
    }
}
