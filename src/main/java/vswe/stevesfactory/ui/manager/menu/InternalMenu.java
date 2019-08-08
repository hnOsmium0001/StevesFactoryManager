package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.widget.NumberField;
import vswe.stevesfactory.ui.manager.editor.Menu;

public class InternalMenu extends Menu {

    private NumberField<Integer> interval = NumberField.integerField(21, 16);

    public InternalMenu() {
        addChildren(interval);
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.Interval");
    }

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {
    }
}
