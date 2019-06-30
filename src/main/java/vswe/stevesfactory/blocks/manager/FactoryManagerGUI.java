package vswe.stevesfactory.blocks.manager;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;
import vswe.stevesfactory.blocks.manager.components.*;
import vswe.stevesfactory.library.gui.background.DisplayListCaches;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.*;

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
        private Dimension dimensions = new Dimension();
        private Dimension contentDimensions = new Dimension();

        private int backgroundDL;

        private TopLevelWidget topLevel;
        private IWidget focusedWidget;

        private PrimaryWindow() {
            this.updateScreenBounds();
            // Make sure the width/height are initialized before we touch the children
            this.topLevel = new TopLevelWidget(this);
            this.focusedWidget = topLevel;
        }

        @Override
        public Dimension getBorder() {
            return dimensions;
        }

        @Override
        public Dimension getContentDimensions() {
            return contentDimensions;
        }

        @Override
        public List<IWidget> getChildren() {
            return ImmutableList.of(topLevel);
        }

        @Override
        public Point getPosition() {
            return screenBounds.getLocation();
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            GlStateManager.callList(backgroundDL);
            topLevel.render(mouseX, mouseY, particleTicks);
//            GuiUtils.drawGradientRect(0, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xaaff9d82, 0xaaff9d82);
//            GuiUtils.drawGradientRect(0, getContentX(), getContentY(), getContentX() + getContentDimensions().width, getContentY() + getContentDimensions().height, 0xaa5c87ff, 0xaa5c87ff);
        }

        @Nonnull
        @Override
        public IWidget getFocusedWidget() {
            return focusedWidget;
        }

        @Override
        public void setFocusedWidget(@Nullable IWidget widget) {
            focusedWidget = MoreObjects.firstNonNull(widget, topLevel);
        }

        public Rectangle getScreenBounds() {
            return screenBounds;
        }

        public void setScreenBounds(int width, int height) {
            // Borders
            this.screenBounds.width = width;
            this.screenBounds.height = height;
            // Centering
            this.screenBounds.x = scaledWidth() / 2 - width / 2;
            this.screenBounds.y = scaledHeight() / 2 - height / 2;

            this.dimensions.width = width;
            this.dimensions.height = height;
            this.contentDimensions.width = width - 4 * 2;
            this.contentDimensions.height = height - 4 * 2;

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

    public static class TopLevelWidget extends AbstractWidget implements IContainer<DynamicWidthWidget<?>>, ContainerWidgetMixin<DynamicWidthWidget<?>> {

        public final SelectionPanel selectionPanel;
        public final EditorPanel editorPanel;
        private final ImmutableList<DynamicWidthWidget<?>> children;

        private TopLevelWidget(PrimaryWindow window) {
            super(window.getContentDimensions().width, window.getContentDimensions().height);
            this.onWindowChanged(window, this);
            this.selectionPanel = new SelectionPanel(this, getWindow());
            this.editorPanel = new EditorPanel(this, getWindow());
            this.children = ImmutableList.of(selectionPanel, editorPanel);
            this.reflow();
        }

        @Nonnull
        @Override
        public IWidget getParentWidget() {
            return this;
        }

        @Override
        public void onWindowChanged(IWindow newWindow, IWidget newParent) {
            Preconditions.checkArgument(newParent == this);
            super.onWindowChanged(newWindow, this);
            setLocation(newWindow.getContentX(), newWindow.getContentY());
        }

        @Override
        public void onParentChanged(IWidget newParent) {
            Preconditions.checkArgument(newParent == this);
        }

        @Override
        public void onParentPositionChanged() {
            // Do nothing because we don't really have a parent widget
        }

        @Override
        public List<DynamicWidthWidget<?>> getChildren() {
            return children;
        }

        @Override
        public DynamicWidthLayout getLayout() {
            return DynamicWidthLayout.INSTANCE;
        }

        @Override
        public TopLevelWidget addChildren(DynamicWidthWidget widget) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TopLevelWidget addChildren(Collection<DynamicWidthWidget<?>> widgets) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            selectionPanel.render(mouseX, mouseY, particleTicks);
            editorPanel.render(mouseX, mouseY, particleTicks);
        }

    }

    public static class DynamicWidthLayout implements ILayout<DynamicWidthWidget<?>> {

        public static final DynamicWidthLayout INSTANCE = new DynamicWidthLayout();

        @Override
        public List<DynamicWidthWidget<?>> reflow(Dimension bounds, List<DynamicWidthWidget<?>> widgets) {
            int usable = bounds.width;
            int nextX = 0;
            for (DynamicWidthWidget widget : widgets) {
                switch (widget.getWidthOccupier()) {
                    case MIN_WIDTH: {
                        int w = calculateWidthMin(widget);
                        widget.setX(nextX);
                        widget.setWidth(w);
                        usable -= w;
                        nextX += w;
                        break;
                    }
                    case MAX_WIDTH: {
                        int w = calculateWidthMax(widget, usable);
                        widget.setX(nextX);
                        widget.setWidth(w);
                        nextX += w;
                        break;
                    }
                }
            }

            return widgets;
        }

        private int calculateWidthMin(DynamicWidthWidget<?> widget) {
            IWidget furthest = widget.getChildren().stream()
                    .max(Comparator.comparingInt(IWidget::getX))
                    .orElseThrow(RuntimeException::new);
            int x = furthest.getX();
            int w = furthest.getWidth();
            return x + w;
        }

        private int calculateWidthMax(DynamicWidthWidget<?> widget, int usable) {
            return usable;
        }

        @Override
        public LayoutType getType() {
            return LayoutType.StatelessLayout;
        }

    }

}
