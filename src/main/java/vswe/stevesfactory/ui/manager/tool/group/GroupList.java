package vswe.stevesfactory.ui.manager.tool.group;

import vswe.stevesfactory.library.gui.widget.box.LinearList;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import java.util.Collection;

public class GroupList extends LinearList<GroupButton> {

    public GroupList() {
        super(64, 0);
        // No need to remove listener, because this list has the same lifetime as the whole GUI
        FactoryManagerGUI.get().groupModel.addListenerAdd(this::onGroupAdded);
        FactoryManagerGUI.get().groupModel.addListenerRemove(this::onGroupRemoved);
        FactoryManagerGUI.get().groupModel.addListenerUpdate(this::onGroupUpdated);
        // Wait for reflow to finish in this tick
        FactoryManagerGUI.get().scheduleTask(__ -> {
            for (String group : FactoryManagerGUI.get().groupModel.getGroups()) {
                this.addChildren(new GroupButton(group));
            }
            this.reflow();
        });
    }

    private void onGroupAdded(String group) {
        addChildren(new GroupButton(group));
        reflow();
    }

    private void onGroupRemoved(String group) {
        int i = 0;
        for (GroupButton child : getChildren()) {
            if (child.getGroup().equals(group)) {
                int index = i; // Effectively final index for lambda capturing
                FactoryManagerGUI.get().scheduleTask(__ -> {
                    getChildren().remove(index);
                    reflow();
                });
            }
            i++;
        }
    }

    private void onGroupUpdated(String from, String to) {
        for (GroupButton child : getChildren()) {
            if (child.getGroup().equals(from)) {
                child.setGroup(to);
            }
        }
    }

    @Override
    public GroupList addChildren(GroupButton widget) {
        super.addChildren(widget);
        widget.setWidth(calcButtonWidth());
        return this;
    }

    @Override
    public GroupList addChildren(Collection<GroupButton> widgets) {
        super.addChildren(widgets);
        for (GroupButton widget : widgets) {
            widget.setWidth(calcButtonWidth());
        }
        return this;
    }

    private int calcButtonWidth() {
        return getBarLeft() - 2;
    }

    @Override
    public int getMarginMiddle() {
        return 2;
    }

    public void delete(String group) {
        FactoryManagerGUI.get().groupModel.removeGroup(group);
//        FactoryManagerGUI gui = FactoryManagerGUI.get();
//        for (FlowComponent<?> component : gui.getTopLevel().editorPanel.getFlowComponents()) {
//            if (component.getGroup().equals(group)) {
//                gui.scheduleTask(__ -> component.remove());
//            }
//        }
    }

}
