package vswe.stevesfactory.ui.manager.menu;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.contextmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.contextmenu.ContextMenu;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.utils.BlockHighlight;

import javax.annotation.Nonnull;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class BlockTarget extends AbstractWidget implements IButton, INamedElement, LeafWidgetMixin {

    private boolean hovered = false;
    private boolean clicked = false;
    private boolean selected = false;

    public final BlockPos pos;

    private BlockState state;
    private ItemStack cachedItemStack;

    public BlockTarget(BlockPos pos) {
        this(pos, 16);
    }

    public BlockTarget(BlockPos pos, int size) {
        super(0, 0, size, size);
        this.pos = pos;
        this.setBlockState(minecraft().world.getBlockState(pos));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            ContextMenu contextMenu = ContextMenu.atCursor(ImmutableList.of(
                    new CallbackEntry(null, "gui.sfm.ActionMenu.BlockTarget.Highlight", b -> BlockHighlight.createHighlight(pos, 80))
            ));
            WidgetScreen.getCurrentScreen().addPopupWindow(contextMenu);
            return true;
        }

        clicked = true;
        setSelected(!selected);
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

    public void setSelected(boolean selected) {
        this.selected = selected;
        InventorySelectionMenu<?> menu = (InventorySelectionMenu<?>) getParentWidget().getParentWidget();
        menu.updateData();
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

        // No depth test so that popups get correctly rendered
        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();
        RenderHelper.enableGUIStandardItemLighting();
        // 16 is the standard item size
        minecraft().getItemRenderer().renderItemIntoGUI(cachedItemStack, x + (getWidth() - 16) / 2, y + (getHeight() - 16) / 2);

        if (hovered) {
            WidgetScreen.getCurrentScreen().setHoveringText(new ItemStack(state.getBlock().asItem()), (int) mouseX, (int) mouseY);
        }

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
