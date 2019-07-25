package vswe.stevesfactory.ui.manager.selection;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.ui.manager.components.EditorPanel;
import vswe.stevesfactory.ui.manager.components.FlowComponent;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ComponentSelectionButton extends AbstractWidget implements RelocatableWidgetMixin, LeafWidgetMixin {

    public enum Component {
        TRIGGER("trigger"),
        ITEM_IMPORT("item_import"),
        ITEM_EXPORT("item_export"),
        ITEM_CONDITION("item_condition"),
        FLOW_CONTROL("flow_control"),
        FLUID_IMPORT("fluid_import"),
        FLUID_EXPORT("fluid_export"),
        FLUID_CONDITION("fluid_condition"),
        REDSTONE_EMITTER("redstone_emitter"),
        REDSTONE_CONDITION("redstone_condition"),
        CRAFT_ITEM("craft_item"),
        FOR_EACH("for_each"),
        GROUP("group"),
        GROUP_IO("group_io"),
        CAMOUFLAGE("camouflage"),
        SIGN_UPDATER("sign_updater"),
        CONFIGURATIONS("configurations");

        public final String id;
        public final ResourceLocation texture;

        Component(String id) {
            this.id = id;
            this.texture = new ResourceLocation(StevesFactoryManager.MODID, "textures/gui/component_icon/" + id + ".png");
        }
    }

    private static final ResourceLocation BACKGROUND_NORMAL = new ResourceLocation(StevesFactoryManager.MODID, "textures/gui/component_background/background_normal.png");
    private static final ResourceLocation BACKGROUND_HOVERED = new ResourceLocation(StevesFactoryManager.MODID, "textures/gui/component_background/background_hovered.png");

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    private final Component component;

    public ComponentSelectionButton(SelectionPanel parent, Component component) {
        super(0, 0, WIDTH, HEIGHT);
        onWindowChanged(parent.getWindow(), parent);
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
        editorPanel.addChildren(new FlowComponent(editorPanel, 1, 3) {
            {
                setName(I18n.format("gui.sfm.Component." + component.id));
                setLocation(10, 10);
            }
        });
        getWindow().setFocusedWidget(this);
        return true;
    }

    public ResourceLocation getTexture() {
        return component.texture;
    }

    @Nonnull
    @Override
    public SelectionPanel getParentWidget() {
        return Objects.requireNonNull((SelectionPanel) super.getParentWidget());
    }
}
