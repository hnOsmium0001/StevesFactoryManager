package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.widget.NumberField;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.ui.manager.editor.Menu;

public class IntervalMenu extends Menu {

    private NumberField<Integer> interval = NumberField.integerField(38, 16)
            .setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE)
            .setValue(1);

    public IntervalMenu() {
        interval.setLocation(20, 30);
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
