package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.layout.ILayout;

import java.util.List;

public interface IContainer extends IWidget {

    List<IWidget> getChildren();

    ILayout getLayout();

}
