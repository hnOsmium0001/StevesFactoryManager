package vswe.stevesfactory.library.gui.window;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.background.BackgroundRenderer;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.box.Box;
import vswe.stevesfactory.library.gui.widget.button.TextButton;
import vswe.stevesfactory.library.gui.window.mixin.NestedEventHandlerMixin;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;

import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledHeight;
import static vswe.stevesfactory.library.gui.screen.WidgetScreen.scaledWidth;

public class Dialogue implements IPopupWindow, NestedEventHandlerMixin {

    public static Dialogue createPrompt(String message, BiConsumer<Integer, String> onConfirm) {
        return createPrompt(message, onConfirm, (b, t) -> {});
    }

    public static Dialogue createPrompt(String message, BiConsumer<Integer, String> onConfirm, BiConsumer<Integer, String> onCancel) {
        return createPrompt(message, "", onConfirm, onCancel);
    }

    public static Dialogue createPrompt(String message, String defaultText, BiConsumer<Integer, String> onConfirm) {
        return createPrompt(message, defaultText, onConfirm, (b, t) -> {});
    }

    public static Dialogue createPrompt(String message, String defaultText, BiConsumer<Integer, String> onConfirm, BiConsumer<Integer, String> onCancel) {
        return createPrompt(message, defaultText, "gui.sfm.Dialogue.OK", "gui.sfm.Dialogue.Cancel", onConfirm, onCancel);
    }

    public static Dialogue createPrompt(String message, String defaultText, String confirm, String cancel, BiConsumer<Integer, String> onConfirm, BiConsumer<Integer, String> onCancel) {
        Dialogue dialogue = new Dialogue();
        dialogue.messageBox.addTranslatedLine(message);

        TextField inputBox = new TextField(0, 0, 0, 16).setText(defaultText);
        // TODO add margin support
//        inputBox.setMarginBotttom(4);
        dialogue.insertBeforeButtons(inputBox);
        dialogue.onPostReflow = inputBox::expandHorizontally;

        // TODO and replace this with bottom margin
        dialogue.insertBeforeButtons(new Spacer(0, 4));

        dialogue.buttons.addChildren(TextButton.of(confirm, b -> onConfirm.accept(b, inputBox.getText())));
        dialogue.bindRemoveSelf2LastButton();
        dialogue.buttons.addChildren(TextButton.of(cancel, b -> onCancel.accept(b, inputBox.getText())));
        dialogue.bindRemoveSelf2LastButton();

        dialogue.reflow();
        dialogue.setFocusedWidget(inputBox);
        return dialogue;
    }

    public static Dialogue createBiSelectionDialogue(String message, IntConsumer onConfirm) {
        return createBiSelectionDialogue(message, onConfirm, TextButton.DUMMY);
    }

    public static Dialogue createBiSelectionDialogue(String message, IntConsumer onConfirm, IntConsumer onCancel) {
        return createBiSelectionDialogue(message, "gui.sfm.Dialogue.OK", "gui.sfm.Dialogue.Cancel", onConfirm, onCancel);
    }

    public static Dialogue createBiSelectionDialogue(String message, String confirm, String cancel, IntConsumer onConfirm, IntConsumer onCancel) {
        Dialogue dialogue = new Dialogue();
        dialogue.messageBox.addTranslatedLine(message);
        dialogue.buttons.addChildren(TextButton.of(confirm, onConfirm));
        dialogue.bindRemoveSelf2LastButton();
        dialogue.buttons.addChildren(TextButton.of(cancel, onCancel));
        dialogue.bindRemoveSelf2LastButton();
        dialogue.reflow();
        return dialogue;
    }

    public static Dialogue createDialogue(String message) {
        return createDialogue(message, TextButton.DUMMY);
    }

    public static Dialogue createDialogue(String message, IntConsumer onConfirm) {
        return createDialogue(message, "gui.sfm.Dialogue.OK", onConfirm);
    }

    public static Dialogue createDialogue(String message, String ok, IntConsumer onConfirm) {
        Dialogue dialogue = new Dialogue();
        dialogue.messageBox.addTranslatedLine(message);
        dialogue.buttons.addChildren(TextButton.of(ok, onConfirm));
        dialogue.bindRemoveSelf2LastButton();
        dialogue.reflow();
        return dialogue;
    }

    private final Point position;
    private final Dimension contents;
    private final Dimension border;

    private Spacer topMargin;
    private TextList messageBox;
    private Box<TextButton> buttons;
    private List<AbstractWidget> children;
    private IWidget focusedWidget;

    public Runnable onPreReflow = () -> {};
    public Runnable onPostReflow = () -> {};

    public Dialogue() {
        this.position = new Point();
        this.contents = new Dimension();
        this.border = new Dimension();
        this.topMargin = new Spacer(0, 5);
        this.messageBox = new TextList(10, 10, new ArrayList<>());
        this.messageBox.setFitContents(true);
        this.buttons = new Box<TextButton>(0, 0, 10, 10)
                .setLayout(b -> {
                    int x = 0;
                    for (TextButton button : b) {
                        button.setLocation(x, 0);
                        x += button.getWidth() + 2;
                    }
                });
        this.children = new ArrayList<>();
        {
            children.add(topMargin);
            children.add(messageBox);
            children.add(buttons);
        }

        updateBorderUsingContent();
        updatePosition();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        BackgroundRenderer.drawVanillaStyle(position.x, position.y, border.width, border.height, 0F);
        GlStateManager.enableAlphaTest();
        for (IWidget child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    public void reflow() {
        onPreReflow.run();
        messageBox.expandHorizontally();
        buttons.reflow();
        buttons.adjustMinContent();

        FlowLayout.INSTANCE.reflow(getContentDimensions(), children);

        updateDimensions();
        onPostReflow.run();
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
        notifyChildren();
    }

    public void notifyChildren() {
        for (AbstractWidget child : children) {
            child.setWindow(this);
        }
    }

    public Spacer getTopMargin() {
        return topMargin;
    }

    public TextList getMessageBox() {
        return messageBox;
    }

    public Box<TextButton> getButtons() {
        return buttons;
    }

    public void insertBeforeButtons(AbstractWidget widget) {
        children.add(children.size() - 1, widget);
    }

    public void appendChild(AbstractWidget widget) {
        children.add(widget);
    }

    @Override
    public int getLifespan() {
        return -1;
    }

    @Override
    public boolean shouldDrag(double mouseX, double mouseY) {
        return false;
    }

    private int initialDragLocalX, initialDragLocalY;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (NestedEventHandlerMixin.super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (isInside(mouseX, mouseY)) {
            setFocusedWidget(null);
            initialDragLocalX = (int) mouseX - position.x;
            initialDragLocalY = (int) mouseY - position.y;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (NestedEventHandlerMixin.super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (isInside(mouseX, mouseY)) {
            initialDragLocalX = -1;
            initialDragLocalY = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (NestedEventHandlerMixin.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (isInside(mouseX, mouseY) && initialDragLocalX != -1 && initialDragLocalY != -1) {
            position.x = (int) mouseX - initialDragLocalX;
            position.y = (int) mouseY - initialDragLocalY;
            notifyChildren();
            return true;
        }
        return false;
    }

    @Override
    public DiscardCondition getDiscardCondition() {
        return DiscardCondition.NONE;
    }

    @Override
    public Dimension getBorder() {
        return border;
    }

    @Override
    public int getBorderSize() {
        // TODO add support for flat border
//        return 2 + 1;
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

    public void bindRemoveSelf(int buttonID) {
        TextButton button = buttons.getChildren().get(buttonID);
        if (button.hasClickAction()) {
            IntConsumer oldAction = button.onClick;
            button.onClick = b -> {
                WidgetScreen.getCurrentScreen().deferRemovePopupWindow(this);
                oldAction.accept(b);
            };
        } else {
            button.onClick = b -> WidgetScreen.getCurrentScreen().deferRemovePopupWindow(this);
        }
    }

    public void bindRemoveSelf2LastButton() {
        bindRemoveSelf(buttons.getChildren().size() - 1);
    }

    @CanIgnoreReturnValue
    public boolean tryAddSelfToActiveGUI() {
        if (Minecraft.getInstance().currentScreen instanceof WidgetScreen) {
            WidgetScreen.getCurrentScreen().addPopupWindow(this);
            return true;
        }
        return false;
    }
}
