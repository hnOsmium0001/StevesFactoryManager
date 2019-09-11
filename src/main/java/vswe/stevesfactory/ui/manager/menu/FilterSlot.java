package vswe.stevesfactory.ui.manager.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.window.PlayerInventoryWindow;

import java.util.Collection;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class FilterSlot extends AbstractWidget implements INamedElement, LeafWidgetMixin {

    private static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(36, 20, 16, 16);
    private static final TextureWrapper HOVERED = NORMAL.down(1);

    private ItemStack stack;

    private Editor editor;

    public FilterSlot(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        if (isInside(mouseX, mouseY)) {
            HOVERED.draw(getAbsoluteX(), getAbsoluteY());
        } else {
            NORMAL.draw(getAbsoluteX(), getAbsoluteY());
        }

        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();
        RenderHelper.enableGUIStandardItemLighting();
        minecraft().getItemRenderer().renderItemIntoGUI(stack, getAbsoluteX(), getAbsoluteY());

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            PlayerInventoryWindow popup = new PlayerInventoryWindow();
            WidgetScreen.getCurrentScreen().addPopupWindow(popup);
            return true;
        }
        if (button == GLFW_MOUSE_BUTTON_RIGHT && !stack.isEmpty()) {
            Editor editor = getDedicatedEditor();
            return true;
        }
        return false;
    }

    private Editor getDedicatedEditor() {
        if (editor == null) {
            editor = new Editor(this);
        }
        return editor;
    }

    @Override
    public String getName() {
        return I18n.format(stack.getTranslationKey());
    }

    public static class Editor extends AbstractContainer<IWidget> {

        private FilterSlot slot;

        public Editor(FilterSlot slot) {
            this.slot = slot;
        }

        @Override
        public Collection<IWidget> getChildren() {
            return null;
        }

        @Override
        public void reflow() {

        }
    }
}
