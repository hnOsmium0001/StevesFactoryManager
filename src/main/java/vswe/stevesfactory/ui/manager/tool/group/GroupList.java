package vswe.stevesfactory.ui.manager.tool.group;

import vswe.stevesfactory.library.gui.widget.box.LinearList;

import java.util.Collection;

import static vswe.stevesfactory.library.gui.RenderingHelper.rectVertices;

public class GroupList extends LinearList<GroupButton> {

    public GroupList() {
        super(64, 0);

//        for (int i = 0; i < 32; i++) {
//            addChildren(new GroupButton());
//        }
//        reflow();
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
}
