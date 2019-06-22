package vswe.stevesfactory.library.gui.layout;

public enum BoxSizing {

    BORDER_BOX(true),
    CONTENT_BOX(true),
    PHANTOM(false),
    ;

    public final boolean flow;

    BoxSizing(boolean flow) {
        this.flow = flow;
    }

}
