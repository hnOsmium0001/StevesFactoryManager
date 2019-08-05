package vswe.stevesfactory.library.gui.window;

import com.mojang.blaze3d.platform.GlStateManager;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.background.DisplayListCaches;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.TextList;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledHeight;
import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledWidth;

public class Dialogue implements IPopupWindow, NestedEventHandlerMixin {

    private final Point position;
    private final Dimension contents;
    private final Dimension border;

    private TextList messageBox;
    private List<IWidget> children = new ArrayList<>();
    private IWidget focusedWidget;

    private int backgroundDL;

    public Dialogue() {
        this.position = new Point();
        this.contents = new Dimension();
        this.border = new Dimension();
        this.messageBox = new TextList(10, 10, new ArrayList<>());
        this.children.add(messageBox);

        this.updatePosition();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        GlStateManager.callList(backgroundDL);
        for (IWidget child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    private void updatePosition() {
        this.position.x = scaledWidth() / 2 - getWidth() / 2;
        this.position.y = scaledHeight() / 2 - getHeight() / 2;
        updateBackgroundDL();
    }

    private void updateBackgroundDL() {
        backgroundDL = DisplayListCaches.createVanillaStyleBackground(getX(), getY(), getWidth(), getHeight());
    }

    public TextList getMessageBox() {
        return messageBox;
    }

    @Override
    public void setPosition(int x, int y) {
        IPopupWindow.super.setPosition(x, y);
        updateBackgroundDL();
    }

    @Override
    public int getLifespan() {
        return -1;
    }

    @Override
    public boolean shouldDrag(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public DiscardCondition getDiscardCondition() {
        return null;
    }

    @Override
    public Dimension getBorder() {
        return border;
    }

    @Override
    public int getBorderSize() {
        return 4;
    }

    @Override
    public Dimension getContentDimensions() {
        return contents;
    }

    @Override
    public List<? extends IWidget> getChildren() {
        return children;
    }

    @Override
    public Point getPosition() {
        return position;
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
}
