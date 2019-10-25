package vswe.stevesfactory.ui.manager.menu;

import com.google.common.collect.Maps;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Direction;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.logic.procedure.IDirectionTarget;
import vswe.stevesfactory.logic.procedure.ILogicalConjunction;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;
import vswe.stevesfactory.utils.VectorHelper;

import java.util.*;

public class RedstoneSidesMenu<P extends IProcedure & IProcedureClientData & IDirectionTarget & ILogicalConjunction> extends Menu<P> {

    public static final int INITIAL_Y = 30;
    private final RadioButton any, all;
    private final Map<Direction, Checkbox> sides;

    private final int id;
    public static final int INITIAL_X = 4;

    public RedstoneSidesMenu(int id) {
        this.id = id;

        RadioController filterTypeController = new RadioController();
        any = new RadioButton(filterTypeController);
        all = new RadioButton(filterTypeController);
        int y = HEADING_BOX.getPortionHeight() + 4;
        any.setLocation(4, y);
        any.translateLabel("gui.sfm.Menu.RequireAll");
        all.setLocation(getWidth() / 2, y);
        all.translateLabel("gui.sfm.Menu.IfAny");
        addChildren(any);
        addChildren(all);

        EnumMap<Direction, Checkbox> sides = new EnumMap<>(Direction.class);
        for (Direction direction : VectorHelper.DIRECTIONS) {
            Checkbox checkbox = new Checkbox();
            checkbox.translateLabel("gui.sfm." + direction.getName());
            addChildren(checkbox);
            sides.put(direction, checkbox);
        }
        //noinspection UnstableApiUsage
        this.sides = Maps.immutableEnumMap(sides);
        reflow();
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        P procedure = getLinkedProcedure();
        any.onChecked = () -> procedure.setConjunctionType(ILogicalConjunction.Type.ANY);
        all.onChecked = () -> procedure.setConjunctionType(ILogicalConjunction.Type.ALL);

        for (Map.Entry<Direction, Checkbox> entry : sides.entrySet()) {
            Checkbox checkbox = entry.getValue();
            checkbox.onStateChange = b -> procedure.setEnabled(id, entry.getKey(), b);
        }
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.RedstoneTrigger.Sides");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }

    @Override
    public void reflow() {
        int x = INITIAL_X;
        int y = HEADING_BOX.getPortionHeight() + INITIAL_Y;
        int i = 1;
        for (Map.Entry<Direction, Checkbox> entry : sides.entrySet()) {
            Checkbox checkbox = entry.getValue();
            checkbox.setLocation(x, y);
            if (i % 2 == 0) {
                x = INITIAL_X;
                y += 10;
            } else {
                x = getWidth() / 2;
            }
            i++;
        }
    }
}
