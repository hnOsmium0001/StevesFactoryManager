package vswe.stevesfactory.ui.manager.tool.group;

import vswe.stevesfactory.library.gui.window.Dialog;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

public class CreateGroupDialog extends Dialog {

    public static Dialog create() {
        return createPrompt(
                "gui.sfm.FactoryManager.Tool.Group.Dialog.CreateGroup",
                (b, name) -> {
                    boolean success = FactoryManagerGUI.get().groupModel.addGroup(name);
                    if (!success) {
                        Dialog.createDialog("gui.sfm.FactoryManager.Tool.Group.Dialog.CreateFailed").tryAddSelfToActiveGUI();
                    }
                });
    }
}
