package vswe.stevesfactory.library.gui.core;

import net.minecraft.client.gui.IGuiEventListener;

import java.awt.*;
import java.util.List;

public interface IWindow<T extends IWidget> extends IGuiEventListener {

    Dimension getBorder();

    Dimension getContentDimensions();

    List<IWidget> getChildren();

    IWidget getFocusedWidget();

    ILayout<T> getLayout();

}
