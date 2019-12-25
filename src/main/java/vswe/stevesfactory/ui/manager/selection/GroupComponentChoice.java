package vswe.stevesfactory.ui.manager.selection;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.contextmenu.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.ui.manager.editor.EditorPanel;

import javax.annotation.Nonnull;
import java.util.*;

public class GroupComponentChoice extends AbstractWidget implements IComponentChoice, LeafWidgetMixin {

    private ComponentGroup group;

    public GroupComponentChoice(ComponentGroup group) {
        super(0, 0, 16, 16);
        this.group = group;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        renderBackground(mouseX, mouseY);
        RenderingHelper.drawCompleteTexture(getAbsoluteX(), getAbsoluteY(), getAbsoluteXRight(), getAbsoluteYBottom(), getIcon());
        if (isInside(mouseX, mouseY)) {
            WidgetScreen.getCurrent().setHoveringText(I18n.format(group.getTranslationKey()), mouseX, mouseY);
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ContextMenu contextMenu;
        if (group.getMembers().isEmpty()) {
            contextMenu = ContextMenu.atCursor(
                    getAbsoluteXRight() + 2, getAbsoluteY(),
                    ImmutableList.of(new DefaultEntry(null, "gui.sfm.FactoryManager.Selection.NoComponentGroupsPresent")));
            WidgetScreen.getCurrent().addPopupWindow(contextMenu);
        } else {
            List<IEntry> entries = new ArrayList<>();
            for (IProcedureType<?> type : group.getMembers()) {
                entries.add(new CallbackEntry(type.getIcon(), type.getLocalizedName(), b -> createFlowComponent(type)));
            }
            contextMenu = ContextMenu.atCursor(getAbsoluteXRight() + 2, getAbsoluteY(), entries);
        }
        WidgetScreen.getCurrent().addPopupWindow(contextMenu);
        getWindow().setFocusedWidget(this);
        return true;
    }

    public ResourceLocation getIcon() {
        return group.getIcon();
    }

    @Nonnull
    @Override
    public SelectionPanel getParentWidget() {
        return Objects.requireNonNull((SelectionPanel) super.getParentWidget());
    }

    @Override
    public EditorPanel getEditorPanel() {
        return getParentWidget().getParentWidget().editorPanel;
    }
}
