package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.TextField.BackgroundStyle;
import vswe.stevesfactory.logic.procedure.IAnalogTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.ArrayList;
import java.util.List;

public class RedstoneStrengthMenu<P extends IProcedure & IProcedureClientData & IAnalogTarget> extends Menu<P> {

    private NumberField<Integer> begin;
    private NumberField<Integer> end;
    private Checkbox invertCondition;

    public RedstoneStrengthMenu() {
        begin = NumberField.integerFieldRanged(33, 12, 1, 1, 15);
        begin.setBackgroundStyle(BackgroundStyle.RED_OUTLINE);
        end = NumberField.integerFieldRanged(33, 12, 1, 1, 15);
        end.setBackgroundStyle(BackgroundStyle.RED_OUTLINE);
        invertCondition = new Checkbox(0, 0, 8, 8);
        invertCondition.translateLabel("gui.sfm.Menu.InvertCondition");
        TextList info = new TextList(getWidth() - 10 * 2, 16, new ArrayList<>());
        info.setFontHeight(6);
        info.addLineSplit(I18n.format("gui.sfm.Menu.RedstoneTrigger.Strength.Info"));
        info.setLocation(4, HEADING_BOX.getPortionHeight() + 2);

        addChildren(begin);
        addChildren(end);
        addChildren(invertCondition);
        addChildren(info);
        reflow();
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        P procedure = getLinkedProcedure();
        // Using method reference causes LambdaConversionError due to type erasure. This is fixed in JDK 9, which is not available to Minecraft (yet)
        // See JDK-8141508
        begin.setValue(procedure.getAnalogBegin());
        begin.onValueUpdated = begin -> procedure.setAnalogBegin(begin);
        end.setValue(procedure.getAnalogEnd());
        end.onValueUpdated = end -> procedure.setAnalogEnd(end);
        invertCondition.setChecked(procedure.isInverted());
        invertCondition.onStateChange = b -> procedure.setInverted(b);
    }

    @Override
    public void reflow() {
        int y = HEADING_BOX.getPortionHeight() + 20;
        begin.alignLeft(4);
        begin.setY(y);
        end.alignRight(getWidth() - 4);
        end.setY(y);
        invertCondition.setLocation(4, y + 20);
    }

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {
        super.renderContents(mouseX, mouseY, particleTicks);
        String text = I18n.format("gui.sfm.Menu.NumberRange");
        RenderingHelper.drawTextCenteredVertically(text, begin.getAbsoluteXRight() + 2, begin.getAbsoluteY(), begin.getAbsoluteYBottom(), 0xff404040);
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.RedstoneTrigger.Strength");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
