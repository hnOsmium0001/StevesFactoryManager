package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.logic.procedure.IntervalTriggerProcedure;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.ArrayList;
import java.util.List;

public class IntervalMenu extends Menu<IntervalTriggerProcedure> {

    public static final int MARGIN_MIDDLE_UNIT_TEXT = 10;

    private final NumberField<Integer> interval;

    public IntervalMenu() {
        interval = NumberField.integerFieldRanged(38, 14, 1, 1, 999);
        int x = RenderingHelper.getXForAlignedCenter(0, getWidth(), interval.getWidth() + MARGIN_MIDDLE_UNIT_TEXT + fontRenderer().getStringWidth(getUnitText()));
        interval.setValue(1)
                .setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE)
                .setLocation(x, 50);

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
    public void onLinkFlowComponent(FlowComponent<IntervalTriggerProcedure> flowComponent) {
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

    public String getUnitText() {
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

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
