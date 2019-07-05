package vswe.stevesfactory.library.gui.window;

import vswe.stevesfactory.library.gui.IWindow;

import java.awt.*;

@FunctionalInterface
public interface IWindowPositionHandler {

    Point resolve(IWindow window);

}
