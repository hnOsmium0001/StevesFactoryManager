package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.logic.procedure.SignUpdaterProcedure;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.List;

public class SignUpdaterLinesMenu extends Menu<SignUpdaterProcedure> {

    private TextField[] textFields = new TextField[4];

    public SignUpdaterLinesMenu() {
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<SignUpdaterProcedure> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        SignUpdaterProcedure procedure = getLinkedProcedure();
        for (int i = 0; i < textFields.length; i++) {
            textFields[i] = new TextField(0, 0, 48, 13)
                    .setBackgroundStyle(TextField.BackgroundStyle.RED_OUTLINE)
                    .setText(procedure.getTexts()[i]);
            addChildren(textFields[i]);
        }
        FlowLayout.vertical(getChildren(), 4, HEADING_BOX.getPortionHeight() + 4, 4);
    }

    @Override
    protected void updateData() {
        SignUpdaterProcedure procedure = getLinkedProcedure();
        for (int i = 0; i < textFields.length; i++) {
            procedure.getTexts()[i] = textFields[i].getText();
        }
    }

    @Override
    public String getHeadingText() {
        return I18n.format("menu.sfm.SignUpdater.Lines");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
