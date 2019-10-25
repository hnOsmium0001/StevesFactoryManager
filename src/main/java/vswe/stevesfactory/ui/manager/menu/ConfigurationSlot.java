package vswe.stevesfactory.ui.manager.menu;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.slot.AbstractItemSlot;
import vswe.stevesfactory.library.gui.window.PlayerInventoryWindow;

import javax.annotation.Nonnull;
import javax.xml.ws.Holder;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public abstract class ConfigurationSlot<E extends IWidget> extends AbstractWidget implements INamedElement, LeafWidgetMixin {

    private static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(36, 20, 16, 16);
    private static final TextureWrapper HOVERED = NORMAL.toDown(1);

    protected ItemStack stack;
    protected E editor;

    public ConfigurationSlot(ItemStack stack) {
        this.stack = stack;
        this.setDimensions(NORMAL.getPortionWidth(), NORMAL.getPortionHeight());
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        GlStateManager.color3f(1F, 1F, 1F);
        int x = getAbsoluteX();
        int y = getAbsoluteY();
        if (isInside(mouseX, mouseY)) {
            HOVERED.draw(x, y);
            if (!stack.isEmpty()) {
                WidgetScreen.getCurrentScreen().setHoveringText(stack, mouseX, mouseY);
            }
        } else {
            NORMAL.draw(x, y);
        }

        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();
        RenderHelper.enableGUIStandardItemLighting();
        ItemRenderer ir = minecraft().getItemRenderer();
        ir.renderItemAndEffectIntoGUI(stack, x, y);
        ir.renderItemOverlayIntoGUI(fontRenderer(), stack, x, y, "");
        RenderHelper.disableStandardItemLighting();

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            onLeftClick();
            return true;
        }
        if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            onRightClick();
            return true;
        }
        return false;
    }

    protected void onLeftClick() {
        openInventoryPopup();
    }

    protected void onRightClick() {
        if (!stack.isEmpty()) {
            openEditor();
        }
    }

    protected abstract boolean hasEditor();

    protected abstract E createEditor();

    public void openEditor() {
        if (!hasEditor()) {
            return;
        }
        if (editor == null) {
            editor = Preconditions.checkNotNull(createEditor());
        }
        getMenu().openEditor(editor);
    }

    public void closeEditor() {
        getMenu().openEditor(null);
    }

    public void openInventoryPopup() {
        Holder<AbstractItemSlot> selected = new Holder<>();
        PlayerInventoryWindow popup = PlayerInventoryWindow.atCursor(in -> new AbstractItemSlot() {
            private ItemStack representative;

            @Override
            public ItemStack getRenderedStack() {
                return in;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (isSelected() || in.isEmpty()) {
                    // Unselect slot
                    selected.value = null;
                    stack = ItemStack.EMPTY;
                    onSetStack();
                } else {
                    // Select and set slot content
                    selected.value = this;
                    stack = getRepresentative();
                    onSetStack();
                }
                return true;
            }

            @Override
            protected void renderBase() {
                super.renderBase();
                if (isSelected() && !in.isEmpty()) {
                    RenderingHelper.useBlendingGLStates();
                    RenderingHelper.drawRect(getAbsoluteX(), getAbsoluteY(), getAbsoluteXRight(), getAbsoluteYBottom(), 0x66ffff00);
                    GlStateManager.disableBlend();
                    GlStateManager.enableTexture();
                }
            }

            private boolean isSelected() {
                return selected.value == this;
            }

            private ItemStack getRepresentative() {
                if (representative == null) {
                    representative = in.copy();
                    representative.setCount(1);
                }
                return representative;
            }
        });
        WidgetScreen.getCurrentScreen().addPopupWindow(popup);
    }

    protected void onSetStack() {
    }

    @Nonnull
    public MultiLayerMenu<?> getMenu() {
        IWidget parentWidget = Objects.requireNonNull(super.getParentWidget());
        return (MultiLayerMenu<?>) Objects.requireNonNull(parentWidget.getParentWidget());
    }

    @Override
    public String getName() {
        return I18n.format(stack.getTranslationKey());
    }
}
