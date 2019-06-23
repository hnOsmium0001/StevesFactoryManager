package vswe.stevesfactory.blocks.manager;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import vswe.stevesfactory.library.gui.background.DisplayListCaches;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.core.IWindow;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class FactoryManagerGUI extends WidgetScreen {

    public static final float WIDTH_PROPORTION = 2f / 3f;
    public static final float HEIGHT_PROPORTION = 3f / 4f;

    protected FactoryManagerGUI() {
        super(new TranslationTextComponent("gui.sfm.factoryManager.title"));
    }

    @Override
    protected void init() {
        super.init();
        initializePrimaryWindow(new PrimaryWindow());
    }

    @Override
    public void resize(@Nonnull Minecraft mc, int newWidth, int newHeight) {
        super.resize(mc, newWidth, newHeight);
        getPrimaryWindow().updateScreenBounds();
    }

    @Override
    public PrimaryWindow getPrimaryWindow() {
        return (PrimaryWindow) super.getPrimaryWindow();
    }

    public static class PrimaryWindow implements IWindow, NestedEventHandlerMixin {

        // The location and dimensions will be set in the constructor
        private Rectangle screenBounds = new Rectangle();
        private Dimension dimension = new Dimension();

        private int backgroundDL;


        private IWidget focusedWidget;

        public PrimaryWindow() {
            this.updateScreenBounds();
        }

        @Override
        public Dimension getBorder() {
            return dimension;
        }

        @Override
        public Dimension getContentDimensions() {
            return dimension;
        }

        @Override
        public List<IWidget> getChildren() {
            return ImmutableList.of();
        }

        @Override
        public Point getPosition() {
            return screenBounds.getLocation();
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            GlStateManager.callList(backgroundDL);
        }

        @Nullable
        @Override
        public IWidget getFocusedWidget() {
            return focusedWidget;
        }

        @Override
        public void setFocusedWidget(@Nullable IWidget widget) {
            focusedWidget = widget;
        }

        public Rectangle getScreenBounds() {
            return screenBounds;
        }

        public void setScreenBounds(int width, int height) {
            // Borders
            this.screenBounds.width = width + 4 * 2;
            this.screenBounds.height = height + 4 * 2;
            // Centering
            this.screenBounds.x = scaledWidth() / 2 - width / 2;
            this.screenBounds.y = scaledHeight() / 2 - height / 2;

            this.dimension.width = width;
            this.dimension.height = height;

            updateBackgroundDL();
        }

        private void updateBackgroundDL() {
            backgroundDL = DisplayListCaches.createVanillaStyleBackground(new Rectangle(screenBounds));
        }

        private void updateScreenBounds() {
            int width = (int) (scaledWidth() * WIDTH_PROPORTION);
            int height = (int) (scaledHeight() * HEIGHT_PROPORTION);
            setScreenBounds(width, height);
        }

    }

}
