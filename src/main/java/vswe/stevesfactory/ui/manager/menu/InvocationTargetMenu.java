package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.ITrigger;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.box.LinearList;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.logic.procedure.FunctionHatProcedure;
import vswe.stevesfactory.logic.procedure.FunctionInvokeProcedure;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.EditorPanel;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static vswe.stevesfactory.library.gui.RenderingHelper.*;

public class InvocationTargetMenu extends Menu<FunctionInvokeProcedure> {

    public static final int HORIZONTAL_MARGIN = 4;
    public static final int VERTICAL_MARGIN = 2;

    private LinearList<ListEntry> options;

    private int selected = -1;

    public InvocationTargetMenu() {
        options = new LinearList<>(getWidth() - HORIZONTAL_MARGIN * 2, getContentHeight() - VERTICAL_MARGIN * 2);
        options.setLocation(HORIZONTAL_MARGIN, HEADING_BOX.getPortionHeight() + VERTICAL_MARGIN);

        addChildren(options);
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<FunctionInvokeProcedure> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        FunctionInvokeProcedure p = getLinkedProcedure();
        FactoryManagerGUI gui = FactoryManagerGUI.getActiveGUI();
        gui.scheduleTask(__ -> {
            EditorPanel editor = gui.getPrimaryWindow().topLevel.editorPanel;
            int i = 0;
            for (FlowComponent<?> component : editor.getFlowComponents()) {
                if (component.getProcedure() instanceof ITrigger) {
                    ITrigger trigger = (ITrigger) component.getProcedure();
                    options.addChildren(new ListEntry(component, i));
                    if (trigger == p.getTarget()) {
                        selected = i;
                    }
                    i++;
                }
            }
            options.reflow();
        });
    }

    @Override
    public String getHeadingText() {
        return I18n.format("menu.sfm.InvocationTarget");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        if (selected == -1) {
            errors.add(I18n.format("error.sfm.InvocationTarget.Unspecified"));
        }
        return errors;
    }

    private class ListEntry extends AbstractWidget implements LeafWidgetMixin {

        private final FlowComponent<?> target;
        private final int index;

        public ListEntry(FlowComponent<?> target, int index) {
            super(0, 0, 38, 14);
            this.target = target;
            this.index = index;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            InvocationTargetMenu.this.selected = index;
            InvocationTargetMenu.this.getLinkedProcedure().setTarget(target.getProcedure());
            return true;
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

            usePlainColorGLStates();
            Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            int x1 = getAbsoluteX();
            int y1 = getAbsoluteY();
            int x2 = getAbsoluteXRight();
            int y2 = getAbsoluteYBottom();
            rectVertices(x1, y1, x2, y2, 0xff8c8c8c);
            rectVertices(x1 + 1, y1 + 1, x2 - 1, y2 - 1, isInside(mouseX, mouseY) ? 0xff737373 : 0xffc9c9c9);
            Tessellator.getInstance().draw();

            String name = target.getName();
            if (target.getProcedure() instanceof FunctionHatProcedure) {
                name += " (" + ((FunctionHatProcedure) target.getProcedure()).getFunctionName() + ")";
            }
            drawTextCenteredVertically(name, x1 + 2, y1, y2, 0xff000000);

            RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
        }
    }
}
