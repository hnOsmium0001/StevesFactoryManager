package vswe.stevesfactory.ui.intake;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import vswe.stevesfactory.blocks.ItemIntakeTileEntity;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.screen.DisplayListCaches;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.Checkbox;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.library.gui.widget.NumberField;
import vswe.stevesfactory.library.gui.window.AbstractWindow;
import vswe.stevesfactory.network.NetworkHandler;
import vswe.stevesfactory.network.PacketSyncIntakeData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemIntakeGUI extends WidgetScreen {

    private ItemIntakeTileEntity intake;

    public ItemIntakeGUI(ItemIntakeTileEntity intake) {
        super(new TranslationTextComponent("gui.sfm.Title.ItemIntake"));
        this.intake = intake;
    }

    @Override
    protected void init() {
        super.init();
        initializePrimaryWindow(new PrimaryWindow());
    }

    @Override
    public void removed() {
        NetworkHandler.sendToServer(new PacketSyncIntakeData(
                Objects.requireNonNull(intake.getWorld()).getDimension().getType(),
                intake.getPos(),
                getRadiusData(), getRenderingData()));
        super.removed();
    }

    private int getRadiusData() {
        return getPrimaryWindow().radius.getValue();
    }

    private boolean getRenderingData() {
        return getPrimaryWindow().rendering.isChecked();
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
        private Checkbox rendering;
        private List<IWidget> children = new ArrayList<>();

        public PrimaryWindow() {
            setContents(WIDTH, HEIGHT);
            updatePosAndDL();

            radius = NumberField.integerFieldRanged(33, 12, 1, 0, intake.getMaximumRadius());
            radius.setWindow(this);
            radius.setValue(intake.getRadius());
            rendering = new Checkbox(0, 0, 8, 8);
            rendering.setWindow(this);
            rendering.setLabel(I18n.format("gui.sfm.ItemIntake.RenderWorkingArea"));
            rendering.setChecked(intake.isRendering());

            children.add(radius);
            children.add(rendering);
            FlowLayout.vertical(children, 0, 0, 2);
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
            RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
        }
    }
}
