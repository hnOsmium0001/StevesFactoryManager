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

    public static String formatGroupName(String group) {
        return group.isEmpty() ? I18n.format("gui.sfm.FactoryManager.Tool.Group.DefaultGroup") : group;
    }

    private String group;

    public GroupButton(String name) {
        this.setGroup(name);
        this.setHeight(12);
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
        WidgetScreen.getCurrent().addPopupWindow(contextMenu);
    }

    private void actionSwitchGroup() {
        FactoryManagerGUI.get().getTopLevel().editorPanel.setCurrentGroup(group);
    }

    private void actionDelete() {
        getGroupList().delete(group);
    }

    private void actionRename() {
        Dialog.createPrompt("gui.sfm.FactoryManager.Tool.Group.Dialog.RenameGroup", (b, newName) -> {
            for (GroupButton group : getGroupList().getChildren()) {
                if (group != this && this.group.equals(group.group)) {
                    Dialog.createDialog(I18n.format("gui.sfm.FactoryManager.Tool.Group.Dialog.RenameFailed", newName)).tryAddSelfToActiveGUI();
                    return;
                }
            }
            FactoryManagerGUI.get().groupModel.updateGroup(group, newName);
        }).tryAddSelfToActiveGUI();
    }

    private void actionMoveContent() {
        SelectGroupDialog.create(toGroup -> {
            for (FlowComponent<?> component : FactoryManagerGUI.get().getTopLevel().editorPanel.getFlowComponents()) {
                if (component.getGroup().equals(group)) {
                    component.getProcedure().setGroup(toGroup);
                }
            }
        }, () -> {
        }).tryAddSelfToActiveGUI();
    }

    private static GroupList getGroupList() {
        return FactoryManagerGUI.get().getTopLevel().toolboxPanel.getGroupList();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
        this.setTextRaw(formatGroupName(group));
    }
}
