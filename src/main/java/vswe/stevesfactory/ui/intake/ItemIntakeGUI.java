package vswe.stevesfactory.ui.intake;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vswe.stevesfactory.blocks.ItemIntakeTileEntity;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.screen.DisplayListCaches;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.window.AbstractWindow;
import vswe.stevesfactory.network.NetworkHandler;
import vswe.stevesfactory.network.PacketSyncIntakeData;

import java.util.*;

public class ItemIntakeGUI extends WidgetScreen<ItemIntakeContainer> {

    public ItemIntakeGUI(ItemIntakeContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        initializePrimaryWindow(new PrimaryWindow());
    }

    @Override
    public void removed() {
        // Server data
        NetworkHandler.sendToServer(new PacketSyncIntakeData(
                Objects.requireNonNull(container.intake.getWorld()).getDimension().getType(),
                container.intake.getPos(),
                container.intake.getRadius(), container.intake.isRendering(), container.intake.getMode()));

        super.removed();
    }

    @Override
    public PrimaryWindow getPrimaryWindow() {
        return (PrimaryWindow) super.getPrimaryWindow();
    }

    public class PrimaryWindow extends AbstractWindow {

        public static final int WIDTH = 180;
        public static final int HEIGHT = 120;

        private int backgroundDL;

        private NumberField<Integer> radius;
        private TextButton mode;
        private Checkbox rendering;
        private List<IWidget> children = new ArrayList<>();

        public PrimaryWindow() {
            setContents(WIDTH, HEIGHT);
            updatePosAndDL();

            radius = NumberField.integerFieldRanged(33, 12, 1, 0, container.intake.getMaximumRadius());
            radius.setWindow(this);
            radius.setValue(container.intake.getRadius());
            radius.setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE);
            radius.onValueUpdated = container.intake::setRadius;
            mode = TextButton.of(container.intake.getMode().statusTranslationKey);
            mode.setWindow(this);
            mode.onClick = b -> {
                container.intake.cycleMode();
                mode.setText(I18n.format(container.intake.getMode().statusTranslationKey));
            };
            rendering = new Checkbox(0, 0, 8, 8);
            rendering.setWindow(this);
            rendering.setLabel(I18n.format("gui.sfm.ItemIntake.RenderWorkingArea"));
            rendering.setChecked(container.intake.isRendering());
            rendering.onStateChange = container.intake::setRendering;

            TextButton btnSaveData = TextButton.of("gui.sfm.ItemIntake.SaveData", b -> onClose());
            btnSaveData.setWindow(this);
            btnSaveData.setWidth(getContentWidth());

            children.add(radius);
            children.add(mode);
            children.add(rendering);
            children.add(btnSaveData);
            FlowLayout.vertical(children, 0, 0, 2);

            btnSaveData.alignBottom(getContentHeight());
        }

        private void updatePosAndDL() {
            centralize();
            backgroundDL = DisplayListCaches.createVanillaStyleBackground(getX(), getY(), getWidth(), getHeight());
        }

        @Override
        public int getBorderSize() {
            return 4;
        }

        @Override
        public List<? extends IWidget> getChildren() {
            return children;
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
            GlStateManager.callList(backgroundDL);
            renderChildren(mouseX, mouseY, particleTicks);
            RenderingHelper.drawTextCenteredVertically(I18n.format("gui.sfm.ItemIntake.Radius"), radius.getAbsoluteXRight() + 2, radius.getAbsoluteY(), radius.getAbsoluteYBottom(), 0xff404040);
            RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
        }
    }
}
