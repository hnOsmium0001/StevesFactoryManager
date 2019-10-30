package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.logic.procedure.RedstoneEmitterProcedure;
import vswe.stevesfactory.logic.procedure.RedstoneEmitterProcedure.OperationType;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.*;

public class EmitterTypeMenu extends Menu<RedstoneEmitterProcedure> {

    private final TextList paragraph;
    private final NumberField<Integer> valueInput;
    private final Map<OperationType, RadioButton> type;

    public EmitterTypeMenu() {
        int y = HEADING_BOX.getPortionHeight() + 2;

        paragraph = new TextList(0, 20, new ArrayList<>());
        paragraph.setLocation(4, y);
        paragraph.setFontHeight(7);
        addChildren(paragraph);

        valueInput = NumberField.integerFieldRanged(33, 12, 15, 1, 15);
        valueInput.alignRight(getWidth() - 4);
        valueInput.setY(y);
        valueInput.setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE);
        paragraph.setWidth(valueInput.getX() - 4 - paragraph.getX());
        addChildren(valueInput);

        RadioController controller = new RadioController();
        type = new EnumMap<>(OperationType.class);
        for (OperationType type : OperationType.VALUES) {
            RadioButton box = new RadioButton(controller);
            box.setLabel(I18n.format(type.nameKey));
            addChildren(box);
            this.type.put(type, box);
        }
        FlowLayout.reflow(4, 25, getWidth(), type);
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<RedstoneEmitterProcedure> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        RedstoneEmitterProcedure procedure = getLinkedProcedure();
        for (Map.Entry<OperationType, RadioButton> entry : type.entrySet()) {
            RadioButton box = entry.getValue();
            OperationType type = entry.getKey();
            box.onChecked = () -> {
                procedure.setOperationType(type);
                paragraph.getTexts().clear();
                paragraph.addLineSplit(I18n.format(type.descriptionKey));
            };
        }
        type.get(procedure.getOperationType()).check(true);
        valueInput.setValue(procedure.getValue());
    }

    @Override
    protected void updateData() {
        RedstoneEmitterProcedure procedure = getLinkedProcedure();
        procedure.setValue(valueInput.getValue());
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.RedstoneEmitter.Type");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
