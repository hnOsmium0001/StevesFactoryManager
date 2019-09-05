package vswe.stevesfactory.ui.manager;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.IWindow;
import vswe.stevesfactory.library.gui.actionmenu.ActionMenu;
import vswe.stevesfactory.library.gui.actionmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.DisplayListCaches;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout;
import vswe.stevesfactory.library.gui.layout.StrictTableLayout.GrowDirection;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.window.NestedEventHandlerMixin;
import vswe.stevesfactory.ui.manager.editor.*;
import vswe.stevesfactory.ui.manager.selection.SelectionPanel;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class FactoryManagerGUI extends WidgetScreen {

    public static final StrictTableLayout DOWN_RIGHT_4_STRICT_TABLE = new StrictTableLayout(GrowDirection.DOWN, GrowDirection.RIGHT, 4);

    ///////////////////////////////////////////////////////////////////////////
    // GUI code
    ///////////////////////////////////////////////////////////////////////////

    public static final int DATA_SYNC_INTERVAL = 6000;

    public static final float WIDTH_PROPORTION = 2F / 3F;
    public static final float HEIGHT_PROPORTION = 3F / 4F;

    public BlockPos controllerPos;

    public FactoryManagerGUI(BlockPos controllerPos) {
        super(new TranslationTextComponent("gui.sfm.factoryManager.title"));
        this.controllerPos = controllerPos;
    }

    @Override
    protected void init() {
        super.init();
        initializePrimaryWindow(new PrimaryWindow());
    }

    @Override
    public void tick() {
        super.tick();
        if (Minecraft.getInstance().world.getGameTime() % DATA_SYNC_INTERVAL == 0) {
            sync();
        }
    }

    @Override
    public void onClose() {
        sync();
        super.onClose();
    }

    private void sync() {
        getPrimaryWindow().topLevel.editorPanel.saveAll();

        INetworkController controller = Objects.requireNonNull((INetworkController) Minecraft.getInstance().world.getTileEntity(controllerPos));
        controller.sync();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (keyCode == GLFW.GLFW_KEY_K) {
//            StevesFactoryManager.logger.info("K pressed:");
//            INetworkController controller = Objects.requireNonNull((INetworkController) Minecraft.getInstance().world.getTileEntity(controllerPos));
//            for (CommandGraph graph : controller.getCommandGraphs()) {
//                StevesFactoryManager.logger.info(graph.collect());
//            }
//        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        public int getBorderSize() {
            return 4;
        }

        @Override
        public Dimension getContentDimensions() {
            return contentDimensions;
        }

        @Override
        public List<? extends IWidget> getChildren() {
            return ImmutableList.of(topLevel);
        }

        @Override
        public Point getPosition() {
            return screenBounds.getLocation();
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
            GlStateManager.callList(backgroundDL);
            topLevel.render(mouseX, mouseY, particleTicks);
            RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
        }

        @Nonnull
        @Override
        public IWidget getFocusedWidget() {
            return focusedWidget;
        }

        @Override
        public void setFocusedWidget(@Nullable IWidget widget) {
            if (focusedWidget != widget) {
                focusedWidget.onFocusChanged(false);
                focusedWidget = MoreObjects.firstNonNull(widget, topLevel);
                focusedWidget.onFocusChanged(true);
            }
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
            this.contentDimensions.width = width - getBorderSize() * 2;
            this.contentDimensions.height = height - getBorderSize() * 2;

            updateBackgroundDL();
        }

        private void updateBackgroundDL() {
            backgroundDL = DisplayListCaches.createVanillaStyleBackground(new Rectangle(screenBounds), 0F);
        }

        private void updateScreenBounds() {
            int width = (int) (scaledWidth() * WIDTH_PROPORTION);
            int height = (int) (scaledHeight() * HEIGHT_PROPORTION);
            setScreenBounds(width, height);
        }
    }

    public static class TopLevelWidget extends AbstractContainer<DynamicWidthWidget<?>> {

        public static final ResourceLocation USER_PREFERENCES_ICON = RenderingHelper.linkTexture("gui/actions/preferences.png");
        public final SelectionPanel selectionPanel;
        public final EditorPanel editorPanel;
        private final ImmutableList<DynamicWidthWidget<?>> children;

        private TopLevelWidget(PrimaryWindow window) {
            super(window);
            this.selectionPanel = new SelectionPanel();
            this.editorPanel = new EditorPanel();
            this.children = ImmutableList.of(selectionPanel, editorPanel);
            this.reflow();
        }

        @Override
        public IWidget getParentWidget() {
            return null;
        }

        @Override
        public void setParentWidget(IWidget newParent) {
            Preconditions.checkArgument(newParent == null);
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
        public void render(int mouseX, int mouseY, float particleTicks) {
            // No render events for this object because it is technically internal for the window, and it has the exact size as the window
            selectionPanel.render(mouseX, mouseY, particleTicks);
            editorPanel.render(mouseX, mouseY, particleTicks);
        }

        @Override
        public void reflow() {
            fillWindow();
            selectionPanel.setParentWidget(this);
            selectionPanel.reflow();
            editorPanel.setParentWidget(this);
            editorPanel.reflow();
            DynamicWidthWidget.reflowDynamicWidth(getDimensions(), children);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            // Fallback action menu
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                ActionMenu actionMenu = ActionMenu.atCursor(mouseX, mouseY, ImmutableList.of(
                        new CallbackEntry(USER_PREFERENCES_ICON, "gui.sfm.ActionMenu.Global.Preferences", button1 -> {
                        })
                ));
                WidgetScreen.getCurrentScreen().addPopupWindow(actionMenu);
            }
            return false;
        }
    }
}
