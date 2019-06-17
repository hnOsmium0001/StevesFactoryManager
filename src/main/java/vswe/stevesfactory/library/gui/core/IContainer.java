package vswe.stevesfactory.library.gui.core;

import java.util.List;

public interface IContainer<T extends IWidget> extends IWidget {

    List<IWidget> getChildren();

    ILayout<T> getLayout();

}
