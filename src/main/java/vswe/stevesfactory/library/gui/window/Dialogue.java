package vswe.stevesfactory.library.gui.window;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.background.DisplayListCaches;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.TextList;
import vswe.stevesfactory.library.gui.widget.box.Box;
import vswe.stevesfactory.library.gui.widget.button.TextButton;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledHeight;
import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledWidth;

public class Dialogue implements IPopupWindow, NestedEventHandlerMixin {

    public static Dialogue createYesNoDialogue(String message, IntConsumer onConfirm, IntConsumer onCancel) {
        Dialogue dialogue = new Dialogue();
        dialogue.messageBox.addTranslatedLine(message);
        dialogue.buttons.addChildren(TextButton.of("gui.sfm.Dialogue.OK", onConfirm));
        dialogue.buttons.addChildren(TextButton.of("gui.sfm.Dialogue.Cancel", onCancel));
        return dialogue;
    }

    public static Dialogue createDialogue(String message, IntConsumer onConfirm) {
        Dialogue dialogue = new Dialogue();
        dialogue.messageBox.addTranslatedLine(message);
        dialogue.buttons.addChildren(TextButton.of("gui.sfm.Dialogue.OK", onConfirm));
        return dialogue;
    }

    public static Dialogue createDialogue(String message) {
        Dialogue dialogue = new Dialogue();
        dialogue.messageBox.addTranslatedLine(message);
        dialogue.buttons.addChildren(TextButton.of("gui.sfm.Dialogue.OK"));
        return dialogue;
    }

    private final Point position;
    private final Dimension contents;
    private final Dimension border;

    private TextList messageBox;
    private Box<TextButton> buttons;
    private List<AbstractWidget> children;
    private IWidget focusedWidget;

    private int backgroundDL;

    public Dialogue() {
        this.position = new Point();
        this.contents = new Dimension();
        this.border = new Dimension();
        this.messageBox = new TextList(10, 10, new ArrayList<>());
        this.messageBox.setFitContents(true);
        this.buttons = new Box<>(0, 0, 10, 10);
        this.buttons.setLayout(b -> {
            int x = 0;
            for (TextButton button : b) {
                button.setLocation(x, 0);
                x += button.getWidth() + 2;
            }
        });
        this.children = ImmutableList.of(messageBox, buttons);

        updateBorderUsingContent();
        updatePosition();
        updateBackgroundDL();
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

    public void reflow() {
        messageBox.setLocation(0, 0);
        messageBox.expandHorizontally();

        buttons.reflow();
        buttons.expandHorizontally();

        FlowLayout.INSTANCE.reflow(getContentDimensions(), children);

        updateDimensions();
        updateBackgroundDL();
    }

    private void updateDimensions() {
        int rightmost = 0;
        int bottommost = 0;
        for (IWidget child : children) {
            int right = child.getX() + child.getWidth();
            int bottom = child.getY() + child.getHeight();
            if (right > rightmost) {
                rightmost = right;
            }
            if (bottom > bottommost) {
                bottommost = bottom;
            }
        }
        contents.width = rightmost;
        contents.height = bottommost;

        updateBorderUsingContent();
        updatePosition();
    }

    private void updateBorderUsingContent() {
        border.width = contents.width + getBorderSize() * 2;
        border.height = contents.height + getBorderSize() * 2;
    }

    private void updatePosition() {
        position.x = scaledWidth() / 2 - getWidth() / 2;
        position.y = scaledHeight() / 2 - getHeight() / 2;
    }

    private void updateBackgroundDL() {
        backgroundDL = DisplayListCaches.createVanillaStyleBackground(getX(), getY(), getWidth(), getHeight());
    }

    public TextList getMessageBox() {
        return messageBox;
    }

    public Box<TextButton> getButtons() {
        return buttons;
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

    public boolean tryAddSelfToActiveGUI() {
        if (Minecraft.getInstance().currentScreen instanceof WidgetScreen) {
            WidgetScreen.getCurrentScreen().addPopupWindow(this);
            return true;
        }
        return false;
    }
}
