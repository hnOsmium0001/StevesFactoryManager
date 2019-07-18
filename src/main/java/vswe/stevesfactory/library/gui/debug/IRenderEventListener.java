package vswe.stevesfactory.library.gui.debug;

import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.IWindow;

public interface IRenderEventListener {

    void onPreRender(IWidget widget, int mx, int my);

    void onPreRender(IWindow window, int mx, int my);

    void onPostRender(IWidget widget, int mx, int my);

    void onPostRender(IWindow window, int mx, int my);
}
