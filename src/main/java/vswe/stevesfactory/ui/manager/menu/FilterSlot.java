package vswe.stevesfactory.ui.manager.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.slot.AbstractItemSlot;
import vswe.stevesfactory.library.gui.window.PlayerInventoryWindow;
import vswe.stevesfactory.utils.RenderingHelper;

import java.util.Collection;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class FilterSlot extends AbstractWidget implements INamedElement, LeafWidgetMixin {

    private static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(36, 20, 16, 16);
    private static final TextureWrapper HOVERED = NORMAL.toDown(1);

    public ItemStack stack;

    private Editor editor;

    public FilterSlot(ItemStack stack) {
        this.stack = stack;
        this.setDimensions(16, 16);
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
            openInventorySelection();
            return true;
        }
        if (button == GLFW_MOUSE_BUTTON_RIGHT && !stack.isEmpty()) {
            Editor editor = getDedicatedEditor();
            return true;
        }
        return false;
    }

    private void openInventorySelection() {
        AbstractItemSlot[] selected = new AbstractItemSlot[1];
        PlayerInventoryWindow popup = new PlayerInventoryWindow(getAbsoluteXRight() + 4, getAbsoluteY(), stack -> new AbstractItemSlot() {
            @Override
            public ItemStack getRenderedStack() {
                return stack;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                selected[0] = isSelected() || stack.isEmpty() ? null : this;
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            protected void renderBase() {
                super.renderBase();
                if (isSelected()) {
                    RenderingHelper.useBlendingGLStates();
                    RenderingHelper.drawRect(getAbsoluteX(), getAbsoluteY(), getAbsoluteXRight(), getAbsoluteYBottom(), 0x66ffff00);
                    GlStateManager.disableBlend();
                    GlStateManager.enableTexture();
                }
            }

            private boolean isSelected() {
                return selected[0] == this;
            }
        }) {
            @Override
            public void onRemoved() {
                if (selected[0] != null) {
                    stack = selected[0].getRenderedStack();
                }
            }
        };
        WidgetScreen.getCurrentScreen().addPopupWindow(popup);
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
