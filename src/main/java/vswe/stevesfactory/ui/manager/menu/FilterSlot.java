package vswe.stevesfactory.ui.manager.menu;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.logic.item.ItemTraitsFilter;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.Menu;

import javax.annotation.Nonnull;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class FilterSlot extends AbstractWidget implements INamedElement, LeafWidgetMixin {

    private static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(36, 20, 16, 16);
    private static final TextureWrapper HOVERED = NORMAL.toDown(1);

    public Runnable onClick = () -> {};
    public ItemStack stack;

    private final ItemTraitsFilter filter;
    private Editor editor;

    public FilterSlot(ItemTraitsFilter filter, ItemStack stack) {
        this.filter = filter;
        this.stack = stack;
        this.setDimensions(16, 16);
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
            onClick.run();
            return true;
        }
        if (button == GLFW_MOUSE_BUTTON_RIGHT && !stack.isEmpty()) {
            openEditor();
            return true;
        }
        return false;
    }

    private Editor getDedicatedEditor() {
        if (editor == null) {
            editor = new Editor();
        }
        return editor;
    }

    private void openEditor() {
        Editor editor = getDedicatedEditor();
        getMenu().openEditor(editor);
        editor.update();
    }

    private void closeEditor() {
        getMenu().openEditor(null);
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

    public class Editor extends AbstractContainer<IWidget> {

        private final NumberField<Integer> count;
        private final NumberField<Integer> damage;
        private final List<IWidget> children;

        public Editor() {
            MultiLayerMenu<?> menu = getMenu();
            setDimensions(menu.getWidth(), menu.getContentHeight());

            TextButton delete = new DeleteFilterButton();
            delete.translate("gui.sfm.Menu.Delete");
            delete.setDimensions(32, 11);
            delete.setLocation(getWidth() - delete.getWidth() - 2, 2);
            delete.onClick = b -> {
                closeEditor();
                stack = ItemStack.EMPTY;
            };
            AbstractIconButton close = new AbstractIconButton(getWidth() - 8 - 1, getHeight() - 8 - 1, 8, 8) {
                @Override
                public void render(int mouseX, int mouseY, float particleTicks) {
                    super.render(mouseX, mouseY, particleTicks);
                    if (isHovered()) {
                        WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("gui.sfm.Menu.CloseEditor.Info"), mouseX, mouseY);
                    }
                }

                @Override
                public TextureWrapper getTextureNormal() {
                    return FactoryManagerGUI.CLOSE_ICON;
                }

                @Override
                public TextureWrapper getTextureHovered() {
                    return FactoryManagerGUI.CLOSE_ICON_HOVERED;
                }

                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    closeEditor();
                    return super.mouseClicked(mouseX, mouseY, button);
                }

                @Override
                public BoxSizing getBoxSizing() {
                    return BoxSizing.PHANTOM;
                }
            };

            count = NumberField.integerFieldRanged(33, 12, 1, 1, Integer.MAX_VALUE)
                    .setValue(stack.getCount());
            count.setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE);
            count.setLabel(I18n.format("gui.sfm.Menu.FilterAmount"));
            damage = NumberField.integerField(33, 12)
                    .setValue(stack.getDamage());
            damage.setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE);
            damage.setLabel(I18n.format("gui.sfm.Menu.FilterDamage"));

            children = ImmutableList.of(close, delete, count, damage);
            reflow();
        }

        @Override
        public Collection<IWidget> getChildren() {
            return children;
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
            super.render(mouseX, mouseY, particleTicks);
            renderItem();
            RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
        }

        private void renderItem() {
            GlStateManager.disableDepthTest();
            GlStateManager.enableTexture();
            RenderHelper.enableGUIStandardItemLighting();
            ItemRenderer ir = minecraft().getItemRenderer();
            int x = getAbsoluteX() + 4;
            int y = getAbsoluteY() + 4;
            ir.renderItemAndEffectIntoGUI(stack, x, y);
            ir.renderItemOverlayIntoGUI(fontRenderer(), stack, x, y, "");
            RenderHelper.disableStandardItemLighting();
            GlStateManager.color3f(1F, 1F, 1F);
        }

        @Override
        public void onRemoved() {
            stack.setCount(count.getValue());
            stack.setDamage(damage.getValue());
        }

        @Override
        public void reflow() {
            FlowLayout.vertical(children, 4, 24, 4);
        }

        public void update() {
            count.setEnabled(filter.isMatchingAmount());
            damage.setEnabled(stack.isDamageable() && filter.isMatchingDamage());
        }
    }

    private static class DeleteFilterButton extends TextButton {

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            super.render(mouseX, mouseY, particleTicks);
            if (isHovered()) {
                WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("gui.sfm.Menu.Delete.Info"), mouseX, mouseY);
            }
        }

        @Override
        protected void renderText() {
            GlStateManager.pushMatrix();
            GlStateManager.enableTexture();
            GlStateManager.translatef(getAbsoluteX() + 2, getAbsoluteY() + 2, 0F);
            GlStateManager.scalef(0.8F, 0.8F, 1F);
            fontRenderer().drawString(getText(), 0, 0, getTextColor());
            GlStateManager.popMatrix();
        }

        @Override
        public int getTextColor() {
            return 0xffaf2727;
        }

        @Override
        public int getNormalBorderColor() {
            return 0xffaf2727;
        }

        @Override
        public int getHoveredBorderColor() {
            return 0xff963737;
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }
    }
}
