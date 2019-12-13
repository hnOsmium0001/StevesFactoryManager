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
import vswe.stevesfactory.logic.item.ItemTraitsFilter;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class FilterSlot extends ConfigurationSlot<FilterSlot.Editor> {

    private final int index;
    private final ItemTraitsFilter filter;

    public FilterSlot(ItemTraitsFilter filter, int index, ItemStack stack) {
        super(stack);
        this.filter = filter;
        this.index = index;
        this.setDimensions(16, 16);
    }

    @Override
    protected boolean hasEditor() {
        return true;
    }

    @Nonnull
    @Override
    protected Editor createEditor() {
        return new Editor();
    }

    @Override
    public void openEditor() {
        super.openEditor();
        editor.update();
    }

    @Override
    protected void onSetStack() {
        filter.getItems().set(index, stack);
    }

    public class Editor extends AbstractContainer<IWidget> {

        private final NumberField<Integer> count;
        private final NumberField<Integer> damage;
        private final List<IWidget> children;

        public Editor() {
            MultiLayerMenu<?> menu = getMenu();
            setDimensions(menu.getWidth(), menu.getContentHeight());

            TextButton delete = new DeleteFilterButton();
            delete.setText(I18n.format("menu.sfm.Delete"));
            delete.setDimensions(32, 11);
            delete.setLocation(getWidth() - delete.getWidth() - 2, 2);
            delete.onClick = b -> {
                closeEditor();
                stack = ItemStack.EMPTY;
                onSetStack();
            };
            AbstractIconButton close = new AbstractIconButton(getWidth() - 8 - 1, getHeight() - 8 - 1, 8, 8) {
                @Override
                public void render(int mouseX, int mouseY, float particleTicks) {
                    super.render(mouseX, mouseY, particleTicks);
                    if (isHovered()) {
                        WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("menu.sfm.CloseEditor.Info"), mouseX, mouseY);
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
            count.setLabel(I18n.format("menu.sfm.ItemFilter.Traits.Amount"));
            count.onValueUpdated = stack::setCount;
            damage = NumberField.integerField(33, 12)
                    .setValue(stack.getDamage());
            damage.setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE);
            damage.setLabel(I18n.format("menu.sfm.ItemFilter.Traits.Damage"));
            damage.onValueUpdated = stack::setDamage;

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
                WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("menu.sfm.Delete.Info"), mouseX, mouseY);
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
