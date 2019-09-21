package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.logic.procedure.TimedTriggerProcedure;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.ArrayList;

public class IntervalMenu extends Menu<TimedTriggerProcedure> {

    public static final int MARGIN_MIDDLE_UNIT_TEXT = 10;

    private final NumberField<Integer> interval = NumberField.integerFieldRanged(38, 14, 1, 1, 999)
            .setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE)
            .setValue(1);

    public IntervalMenu() {
        int x = RenderingHelper.getXForAlignedCenter(0, getWidth(), interval.getWidth() + MARGIN_MIDDLE_UNIT_TEXT + fontRenderer().getStringWidth(getUnitText()));
        interval.setLocation(x, 50);

        int desX = interval.getX();
        TextList description = new TextList(getWidth() - x * 2, 0, new ArrayList<>());
        description.setLocation(desX, HEADING_BOX.getPortionHeight() + 8);
        description.setFitContents(true);
        description.addLineSplit(getWidth() - 4 * 2, I18n.format("gui.sfm.Menu.Interval.Info"));
        description.setFontHeight(8);

        addChildren(interval);
        addChildren(description);
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<TimedTriggerProcedure> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        interval.setValue(getLinkedProcedure().interval / 20);
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.Interval");
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        super.render(mouseX, mouseY, particleTicks);
    }

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {
        super.renderContents(mouseX, mouseY, particleTicks);
        RenderingHelper.drawTextCenteredVertically(
                getUnitText(),
                interval.getAbsoluteXRight() + MARGIN_MIDDLE_UNIT_TEXT,
                interval.getAbsoluteY(), interval.getAbsoluteYBottom(),
                0x000000);
    }

    private String getUnitText() {
        return I18n.format("gui.sfm.seconds");
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
