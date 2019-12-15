package vswe.stevesfactory.ui.manager.tool.group;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.contextmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.contextmenu.ContextMenu;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.TextButton;
import vswe.stevesfactory.library.gui.window.Dialog;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class GroupButton extends TextButton {

    private final String groupName;

    public GroupButton(String name) {
        this.groupName = name;
        setText(name.isEmpty() ? I18n.format("gui.sfm.FactoryManager.Tool.Group.DefaultGroup") : name);
        setHeight(12);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT:
                actionSwitchGroup();
                return true;
            case GLFW_MOUSE_BUTTON_RIGHT:
                openContextMenu();
                return true;
        }
        return false;
    }

    private void openContextMenu() {
        ContextMenu contextMenu = ContextMenu.atCursor(ImmutableList.of(
                new CallbackEntry(FactoryManagerGUI.DELETE_ICON, "gui.sfm.FactoryManager.Tool.Group.CtxMenu.Delete", b -> actionDelete()),
                new CallbackEntry(null, "gui.sfm.FactoryManager.Tool.Group.CtxMenu.RenameGroup", b -> actionRename()),
                new CallbackEntry(null, "gui.sfm.FactoryManager.Tool.Group.CtxMenu.MoveContent", b -> actionMoveContent())
        ));
        WidgetScreen.getCurrentScreen().addPopupWindow(contextMenu);
    }

    private void actionSwitchGroup() {
        FactoryManagerGUI.getActiveGUI().getTopLevel().editorPanel.setCurrentGroup(groupName);
    }

    private void actionDelete() {
        GroupList groupList = getGroupList();
        groupList.delete(groupName);
        groupList.onProcedureGroupChanged();
    }

    private void actionRename() {
        Dialog.createPrompt("gui.sfm.FactoryManager.Tool.Group.PopupMsg.RenameGroup", (b, newName) -> {
            GroupList groupList = getGroupList();
            for (GroupButton group : groupList.getChildren()) {
                if (group != this && groupName.equals(group.groupName)) {
                    Dialog.createDialog(I18n.format("gui.sfm.FactoryManager.Tool.Group.PopupMsg.RenameFailed", newName)).tryAddSelfToActiveGUI();
                    return;
                }
            }
            groupList.move(groupName, newName);
            groupList.onProcedureGroupChanged();
        }).tryAddSelfToActiveGUI();
    }

    private void actionMoveContent() {
        Dialog.createPrompt("gui.sfm.FactoryManager.Tool.Group.PopupMsg.MoveContent", (b, toGroup) -> {
            GroupList groupList = getGroupList();
            boolean exists = false;
            for (GroupButton group : groupList.getChildren()) {
                if (toGroup.equals(group.groupName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                Dialog.createDialog(I18n.format("gui.sfm.FactoryManager.Tool.Group.PopupMsg.MoveFailed", toGroup)).tryAddSelfToActiveGUI();
                return;
            }
            groupList.move(groupName, toGroup);
            groupList.onProcedureGroupChanged();
        }).tryAddSelfToActiveGUI();
    }

    private GroupList getGroupList() {
        return FactoryManagerGUI.getActiveGUI().getTopLevel().toolboxPanel.getGroupList();
    }

    public String getGroupName() {
        return groupName;
    }
}
