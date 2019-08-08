package vswe.stevesfactory.ui.manager.selection;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.EditorPanel;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ComponentSelectionButton extends AbstractWidget implements RelocatableWidgetMixin, LeafWidgetMixin {

    private static final ResourceLocation BACKGROUND_NORMAL = new ResourceLocation(StevesFactoryManager.MODID, "textures/gui/component_background/background_normal.png");
    private static final ResourceLocation BACKGROUND_HOVERED = new ResourceLocation(StevesFactoryManager.MODID, "textures/gui/component_background/background_hovered.png");

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    private final IProcedureType<IProcedure> component;

    public ComponentSelectionButton(SelectionPanel parent, IProcedureType<IProcedure> component) {
        super(0, 0, WIDTH, HEIGHT);
        this.setParentWidget(parent);
        this.component = component;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        int x1 = getAbsoluteX();
        int y1 = getAbsoluteY();
        int x2 = getAbsoluteXBR();
        int y2 = getAbsoluteYBR();
        if (isInside(mouseX, mouseY)) {
            RenderingHelper.drawCompleteTexture(x1, y1, x2, y2, BACKGROUND_HOVERED);
        } else {
            RenderingHelper.drawCompleteTexture(x1, y1, x2, y2, BACKGROUND_NORMAL);
        }
        RenderingHelper.drawCompleteTexture(x1, y1, x2, y2, getTexture());
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        EditorPanel editorPanel = getParentWidget().getParentWidget().editorPanel;
        // TODO actual instance transferring between sides
        BlockPos controllerPos = ((FactoryManagerGUI) WidgetScreen.getCurrentScreen()).controllerPos;
        INetworkController controller = (INetworkController) Minecraft.getInstance().world.getTileEntity(controllerPos);
        FlowComponent flowComponent = component.createWidget(component.createInstance(controller));
        editorPanel.addChildren(flowComponent);
        return true;
    }

    public ResourceLocation getTexture() {
        return component.getIcon();
    }

    @Nonnull
    @Override
    public SelectionPanel getParentWidget() {
        return Objects.requireNonNull((SelectionPanel) super.getParentWidget());
    }
}
