package vswe.stevesfactory.ui.manager;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.contextmenu.DefaultEntry;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.IWidget;

import java.util.Collection;

public class UserPreferencesPanel extends AbstractContainer<IWidget> {

    @Override
    public Collection<IWidget> getChildren() {
        return ImmutableList.of();
    }

    @Override
    public void reflow() {
    }

    public static class OpenerEntry extends DefaultEntry {

        public static final ResourceLocation ICON = RenderingHelper.linkTexture("gui/actions/preferences.png");

        public OpenerEntry() {
            super(ICON, "gui.sfm.ContextMenu.UserPreferences");
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // TODO open user preferences panel
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
