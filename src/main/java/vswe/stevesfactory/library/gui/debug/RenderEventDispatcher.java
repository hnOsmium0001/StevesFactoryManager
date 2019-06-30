package vswe.stevesfactory.library.gui.debug;

import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.core.IWindow;
import vswe.stevesfactory.library.gui.debug.highlight.BoxHighlighting;

public class RenderEventDispatcher {

    public static final boolean DEBUG = false;

    public static void onPreRender(IWidget widget, int mx, int my) {

    }

    public static void onPreRender(IWindow window, int mx, int my) {

    }

    public static void onPostRender(IWidget widget, int mx, int my) {
        if (!DEBUG) {
            return;
        }
        BoxHighlighting.tryDraw(widget, mx, my);
    }

    public static void onPostRender(IWindow window, int mx, int my) {
        if (!DEBUG) {
            return;
        }
        BoxHighlighting.tryDraw(window, mx, my);
    }

}
