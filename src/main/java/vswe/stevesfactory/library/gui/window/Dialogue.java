package vswe.stevesfactory.library.gui.window;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.background.BackgroundRenderers;
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
import java.util.function.*;

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
        Dialogue dialogue = dialogue(message);

        TextField inputBox = new TextField(0, 0, 0, 16).setText(defaultText);
//        inputBox.setMarginBotttom(4);
        dialogue.insertBeforeButtons(inputBox);
        dialogue.onPostReflow = inputBox::expandHorizontally;

        // TODO add margin support
        dialogue.insertBeforeButtons(new Spacer(0, 4));

        dialogue.buttons.addChildren(TextButton.of(confirm, b -> onConfirm.accept(b, inputBox.getText())));
        dialogue.bindRemoveSelf2LastButton();
        dialogue.buttons.addChildren(TextButton.of(cancel, b -> onCancel.accept(b, inputBox.getText())));
        dialogue.bindRemoveSelf2LastButton();

        dialogue.reflow();
        dialogue.centralize();
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
        Dialogue dialogue = dialogue(message);

        dialogue.buttons.addChildren(TextButton.of(confirm, onConfirm));
        dialogue.bindRemoveSelf2LastButton();
        dialogue.buttons.addChildren(TextButton.of(cancel, onCancel));
        dialogue.bindRemoveSelf2LastButton();

        dialogue.reflow();
        dialogue.centralize();
        return dialogue;
    }

    public static Dialogue createDialogue(String message) {
        return createDialogue(message, TextButton.DUMMY);
    }

    public static Dialogue createDialogue(String message, IntConsumer onConfirm) {
        return createDialogue(message, "gui.sfm.Dialogue.OK", onConfirm);
    }

    public static Dialogue createDialogue(String message, String ok, IntConsumer onConfirm) {
        Dialogue dialogue = dialogue(message);

        dialogue.buttons.addChildren(TextButton.of(ok, onConfirm));
        dialogue.bindRemoveSelf2LastButton();

        dialogue.reflow();
        dialogue.centralize();
        return dialogue;
    }

    private static Dialogue dialogue(String message) {
        Dialogue dialogue = new Dialogue();
        // TODO and replace this with bottom margin
        dialogue.insertBeforeMessage(new Spacer(0, 5));
        dialogue.messageBox.addTranslatedLine(message);
        return dialogue;
    }

    public static final Consumer<Dialogue> VANILLA_STYLE_RENDERER = d -> BackgroundRenderers.drawVanillaStyle(d.position.x, d.position.y, d.border.width, d.border.height, 0F);
    public static final int VANILLA_STYLE_BORDER_SIZE = 4;

    public static final Consumer<Dialogue> FLAT_STYLE_RENDERER = d -> {
        GlStateManager.disableAlphaTest();
        BackgroundRenderers.drawFlatStyle(d.position.x, d.position.y, d.border.width, d.border.height, 0F);
        GlStateManager.enableAlphaTest();
    };
    public static final int FLAT_STYLE_BORDER_SIZE = 2 + 1;

    private final Point position;
    private final Dimension contents;
    private final Dimension border;

    private Consumer<Dialogue> backgroundRenderer;
    private int borderSize;

    private TextList messageBox;
    private Box<TextButton> buttons;
    private List<AbstractWidget> children;
    private IWidget focusedWidget;

    public Runnable onPreReflow = () -> {};
    public Runnable onPostReflow = () -> {};

    private int initialDragLocalX, initialDragLocalY;

    public Dialogue() {
        this.position = new Point();
        this.contents = new Dimension();
        this.border = new Dimension();
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
            children.add(messageBox);
            children.add(buttons);
        }
        this.useVanillaBorders();

        centralize();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        backgroundRenderer.accept(this);
        for (IWidget child : children) {
            child.render(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    public void centralize() {
        position.x = scaledWidth() / 2 - getWidth() / 2;
        position.y = scaledHeight() / 2 - getHeight() / 2;
        notifyChildren();
    }

    public void reflow() {
        onPreReflow.run();
        messageBox.expandHorizontally();
        buttons.reflow();
        buttons.adjustMinContent();

        FlowLayout.reflow(children);

        updateDimensions();
        notifyChildren();
        onPostReflow.run();
    }

    private void updateDimensions() {
        updateContentDimensions();
        updateBorderDimensions();
    }

    private void updateContentDimensions() {
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
    }

    private void updateBorderDimensions() {
        border.width = contents.width + getBorderSize() * 2;
        border.height = contents.height + getBorderSize() * 2;
    }

    public void notifyChildren() {
        for (AbstractWidget child : children) {
            // This will update the absolute position as well (because we know all the children are at least AbstractWidget)
            // so no need to call child.onParentPositionChanged() here
            child.setWindow(this);
        }
    }

    public TextList getMessageBox() {
        return messageBox;
    }

    public Box<TextButton> getButtons() {
        return buttons;
    }

    public void insertBeforeMessage(AbstractWidget widget) {
        children.add(0, widget);
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
        if (isInside(mouseX, mouseY) && isDragging()) {
            position.x = (int) mouseX - initialDragLocalX;
            position.y = (int) mouseY - initialDragLocalY;
            notifyChildren();
            return true;
        }
        return false;
    }

    private boolean isDragging() {
        return initialDragLocalX != -1 && initialDragLocalY != -1;
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
        return borderSize;
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

    public void setStyle(Consumer<Dialogue> renderer, int borderSize) {
        this.backgroundRenderer = renderer;
        this.borderSize = borderSize;
        reflow();
    }

    public void useFlatBorders() {
        setStyle(FLAT_STYLE_RENDERER, FLAT_STYLE_BORDER_SIZE);
    }

    public void useVanillaBorders() {
        setStyle(VANILLA_STYLE_RENDERER, VANILLA_STYLE_BORDER_SIZE);
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
