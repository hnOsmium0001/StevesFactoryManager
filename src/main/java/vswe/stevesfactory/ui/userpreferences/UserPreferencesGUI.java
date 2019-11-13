package vswe.stevesfactory.ui.userpreferences;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.contextmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.contextmenu.IEntry;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.library.gui.window.AbstractWindow;

import java.util.List;

import static vswe.stevesfactory.library.gui.widget.AbstractWidget.minecraft;

// TODO GUI and contents
public class UserPreferencesGUI extends WidgetScreen {

    public static final ResourceLocation CONTEXT_MENU_ENTRY_ICON = RenderingHelper.linkTexture("gui/actions/preferences.png");
    public static final String CONTEXT_MENU_ENTRY_NAME = "gui.sfm.ContextMenu.UserPreferences";

    public static IEntry createContextMenuEntry() {
        return new CallbackEntry(CONTEXT_MENU_ENTRY_ICON, CONTEXT_MENU_ENTRY_NAME, b -> replaceCurrentScreen());
    }

    public static void replaceCurrentScreen() {
        minecraft().displayGuiScreen(new UserPreferencesGUI(minecraft().currentScreen));
    }

    public static void replace(Screen screen) {
        minecraft().displayGuiScreen(new UserPreferencesGUI(screen));
    }

    private Screen previousGUI;

    protected UserPreferencesGUI(Screen previousGUI) {
        super(new TranslationTextComponent("gui.sfm.UserPreferences.Title"));
        this.previousGUI = previousGUI;
    }

    @Override
    public void removed() {
        super.removed();
        getMinecraft().displayGuiScreen(previousGUI);
    }

    @Override
    protected void init() {
        super.init();
        initializePrimaryWindow(new PrimaryWindow());
    }

    public static class PrimaryWindow extends AbstractWindow {

        @Override
        public int getBorderSize() {
            return 4;
        }

        @Override
        public List<? extends IWidget> getChildren() {
            return ImmutableList.of();
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
            renderChildren(mouseX, mouseY, particleTicks);
            RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
        }
    }
}
