package vswe.stevesfactory.library.gui;

import java.util.List;

public interface ILayoutContainer {

    List<IWidget> getChildren();

    ILayout getLayout();

}
