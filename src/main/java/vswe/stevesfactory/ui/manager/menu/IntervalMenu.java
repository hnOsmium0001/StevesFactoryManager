package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.widget.NumberField;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.logic.procedure.TimedTriggerProcedure;
import vswe.stevesfactory.ui.manager.editor.Menu;
import vswe.stevesfactory.utils.RenderingHelper;

public class IntervalMenu extends Menu<TimedTriggerProcedure> {

    public static final int MARGIN_MIDDLE_UNIT_TEXT = 10;

    private NumberField<Integer> interval = NumberField.integerField(38, 14)
            .setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE)
            .setValue(1);

    public IntervalMenu() {
        int x = RenderingHelper.getXForAlignedCenter(0, getWidth(), interval.getWidth() + MARGIN_MIDDLE_UNIT_TEXT + fontRenderer().getStringWidth(getUnitText()));
        interval.setLocation(x, 50);
        addChildren(interval);
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.Interval");
    }

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {
        RenderingHelper.drawTextCenteredVertically(
                getUnitText(),
                interval.getAbsoluteXBR() + MARGIN_MIDDLE_UNIT_TEXT,
                interval.getAbsoluteY(), interval.getAbsoluteYBR(),
                0x000000);
    }

    private String getUnitText() {
        return I18n.format("gui.sfm.General.Seconds");
    }

    public int getIntervalSeconds() {
        return interval.getValue();
    }

    public int getIntervalTicks() {
        return getIntervalSeconds() * 20;
    }

    @Override
    protected void updateData() {
        getLinkedProcedure().interval = getIntervalTicks();
    }
}
