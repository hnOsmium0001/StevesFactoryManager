package vswe.stevesfactory.library.gui.window;

import net.minecraft.client.gui.IGuiEventListener;
import vswe.stevesfactory.library.gui.widget.IWidget;

import java.awt.*;
import java.util.List;

public interface IWindow extends IGuiEventListener {

    Dimension getBorder();

    Dimension getContentDimensions();

    List<IWidget> getChildren();



}
