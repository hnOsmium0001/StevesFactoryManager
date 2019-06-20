package vswe.stevesfactory.library.gui.core;

import java.util.List;

public interface IContainer<T extends IWidget> extends IWidget {

    List<T> getChildren();

    ILayout<T> getLayout();

}
