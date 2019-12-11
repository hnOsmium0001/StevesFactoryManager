package vswe.stevesfactory.ui.manager.toolbox;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.tool.ToolPanel;

public class ClosePanelButton extends AbstractIconButton {

    public ClosePanelButton() {
        super(0, 0, 8, 8);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        super.render(mouseX, mouseY, particleTicks);
        if (isInside(mouseX, mouseY)) {
            WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("gui.sfm.FactoryManager.Toolbox.CloseToolPanel"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ToolPanel panel = FactoryManagerGUI.getActiveGUI().getPrimaryWindow().topLevel.toolPanel;
        panel.setActivePanel(null);
        return true;
    }

    @Override
    public TextureWrapper getTextureNormal() {
        return FactoryManagerGUI.CLOSE_ICON;
    }

    @Override
    public TextureWrapper getTextureHovered() {
        return FactoryManagerGUI.CLOSE_ICON_HOVERED;
    }

    @Override
    public BoxSizing getBoxSizing() {
        return BoxSizing.PHANTOM;
    }
}
