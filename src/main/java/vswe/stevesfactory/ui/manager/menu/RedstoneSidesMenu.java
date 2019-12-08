package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.util.Direction;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IClientDataStorage;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.logic.procedure.IDirectionTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;
import vswe.stevesfactory.utils.Utils;

import java.util.*;
import java.util.function.BooleanSupplier;

public class RedstoneSidesMenu<P extends IProcedure & IClientDataStorage & IDirectionTarget> extends Menu<P> {

    private final Map<Direction, Checkbox> sides;

    private final String menuName;
    private final int id;

    public RedstoneSidesMenu(int id, BooleanSupplier firstOptionGetter, Runnable firstOptionSetter, String firstOptionName, BooleanSupplier secondOptionGetter, Runnable secondOptionSetter, String secondOptionName, String menuName, String infoText) {
        this.id = id;
        this.menuName = menuName;

        RadioController filterTypeController = new RadioController();
        RadioButton firstOption = new RadioButton(filterTypeController);
        RadioButton secondOption = new RadioButton(filterTypeController);
        int y = HEADING_BOX.getPortionHeight() + 4;
        firstOption.setLocation(4, y);
        firstOption.setLabel(firstOptionName);
        firstOption.check(firstOptionGetter.getAsBoolean());
        firstOption.onChecked = firstOptionSetter;
        secondOption.setLocation(getWidth() / 2, y);
        secondOption.setLabel(secondOptionName);
        secondOption.check(secondOptionGetter.getAsBoolean());
        secondOption.onChecked = secondOptionSetter;
        addChildren(firstOption);
        addChildren(secondOption);

        sides = new EnumMap<>(Direction.class);
        for (Direction direction : Utils.DIRECTIONS) {
            Checkbox box = new Checkbox();
            box.translateLabel("gui.sfm." + direction.getName());
            addChildren(box);
            sides.put(direction, box);
        }
        FlowLayout.reflow(4, 30, getWidth(), sides);

        TextList info = new TextList(getWidth() - 4 * 2, 16, new ArrayList<>());
        info.setFontHeight(6);
        info.addLineSplit(infoText);
        info.setLocation(4, firstOption.getYBottom() + 2);
        addChildren(info);
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        P procedure = getLinkedProcedure();
        for (Map.Entry<Direction, Checkbox> entry : sides.entrySet()) {
            Checkbox box = entry.getValue();
            box.setChecked(procedure.isEnabled(id, entry.getKey()));
            box.onStateChange = b -> procedure.setEnabled(id, entry.getKey(), b);
        }
    }

    @Override
    public String getHeadingText() {
        return menuName;
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }

}
