package vswe.stevesfactory.library.gui.debug;

import vswe.stevesfactory.library.IWidget;
import vswe.stevesfactory.library.IWindow;
import vswe.stevesfactory.library.gui.debug.highlight.BoxHighlighting;

public final class RenderEventDispatcher {

    private RenderEventDispatcher() {
    }

    private enum Type {
        ENABLED {
            @Override
            public void preRender(IWidget widget, int mx, int my) {

            }

            @Override
            public void preRender(IWindow widget, int mx, int my) {

            }

            @Override
            public void postRender(IWidget widget, int mx, int my) {

                BoxHighlighting.tryDraw(widget, mx, my);
            }

            @Override
            public void postRender(IWindow widget, int mx, int my) {

                BoxHighlighting.tryDraw(widget, mx, my);
            }
        },
        DISABLED {
            // Do nothing at all
            @Override
            public void preRender(IWidget widget, int mx, int my) {
            }

            @Override
            public void preRender(IWindow widget, int mx, int my) {
            }

            @Override
            public void postRender(IWidget widget, int mx, int my) {
            }

            @Override
            public void postRender(IWindow widget, int mx, int my) {
            }
        };

        public abstract void preRender(IWidget widget, int mx, int my);

        public abstract void preRender(IWindow widget, int mx, int my);

        public abstract void postRender(IWidget widget, int mx, int my);

        public abstract void postRender(IWindow widget, int mx, int my);
    }

    private static Type activeInstance = Type.DISABLED;

    public static void onPreRender(IWidget widget, int mx, int my) {
        activeInstance.preRender(widget, mx, my);
    }

    public static void onPreRender(IWindow window, int mx, int my) {
        activeInstance.preRender(window, mx, my);
    }

    public static void onPostRender(IWidget widget, int mx, int my) {
        activeInstance.postRender(widget, mx, my);
    }

    public static void onPostRender(IWindow window, int mx, int my) {
        activeInstance.postRender(window, mx, my);
    }

}
