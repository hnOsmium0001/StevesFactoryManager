package vswe.stevesfactory.library.gui;

import java.awt.*;
import java.util.List;

public interface IWindow {

    Dimension getBorder();

    Dimension getContentDimensions();

    List<IWidget> getChildren();

}
