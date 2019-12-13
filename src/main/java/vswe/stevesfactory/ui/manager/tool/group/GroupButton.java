package vswe.stevesfactory.ui.manager.tool.group;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.widget.TextButton;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

public class GroupButton extends TextButton {

    private final String groupName;

    public GroupButton(String name) {
        this.groupName = name;
        setText(name.isEmpty() ? I18n.format("gui.sfm.FactoryManager.Tool.Group.DefaultGroup") : name);
        setHeight(12);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        FactoryManagerGUI.getActiveGUI().getTopLevel().editorPanel.setCurrentGroup(groupName);
        return true;
    }
}
