package vswe.stevesfactory.ui.manager.tool.group;

import vswe.stevesfactory.library.gui.widget.box.LinearList;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import java.util.*;

import static vswe.stevesfactory.library.gui.RenderingHelper.rectVertices;

public class GroupList extends LinearList<GroupButton> {

    public GroupList() {
        super(64, 0);
        onProcedureGroupChanged();
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

    public int calcButtonWidth() {
        return getBarLeft() - 2;
    }

    @Override
    public int getMarginMiddle() {
        return 2;
    }

    public void onProcedureGroupChanged() {
        getChildren().clear();
        Set<String> existing = new HashSet<>();
        for (FlowComponent<?> component : FactoryManagerGUI.getActiveGUI().getTopLevel().editorPanel.getFlowComponents()) {
            String group = component.getGroup();
            if (existing.contains(group)) {
                continue;
            }
            existing.add(group);
            GroupButton btn = new GroupButton(group);
            addChildren(btn);
        }
        reflow();
    }
}
