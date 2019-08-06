package vswe.stevesfactory.library.gui.actionmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;

import java.awt.*;
import java.util.List;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static vswe.stevesfactory.library.gui.actionmenu.AbstractEntry.HALF_MARGIN_SIDES;
import static vswe.stevesfactory.library.gui.actionmenu.AbstractEntry.MARGIN_SIDES;

public class Section extends AbstractContainer<IEntry> {

    private static final float LINE_COLOR = 125F / 256F;

    private List<IEntry> entries = new ArrayList<>();

    // Relative position is (0,0) by default

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        if (!getWindow().isLastSection(this)) {
            renderLine();
        }
    }

    private void renderLine() {
        int bx = getAbsoluteX() + HALF_MARGIN_SIDES;
        int bx2 = getAbsoluteXBR() - 1;
        int by = getAbsoluteY() + 1;
        GlStateManager.disableTexture();
        GlStateManager.color3f(LINE_COLOR, LINE_COLOR, LINE_COLOR);
        glLineWidth(1F);
        glBegin(GL_LINE);
        glVertex2f(bx, by);
        glVertex2f(bx2, by);
        glEnd();
        GlStateManager.enableTexture();
    }

    public void attach(ActionMenu actionMenu) {
        setWindow(actionMenu);
        Dimension bounds = getDimensions();
        bounds.width = actionMenu.getWidth() - MARGIN_SIDES * 2;
        bounds.height = MARGIN_SIDES;

        for (IEntry entry : entries) {
            entry.attach(actionMenu);
        }
    }

    @Override
    public void reflow() {
        int w = entries.stream()
                .max(Comparator.comparingInt(IEntry::getWidth))
                .orElseThrow(IllegalArgumentException::new)
                .getWidth();
        int h = entries.stream()
                .mapToInt(IEntry::getHeight)
                .sum();
        setDimensions(w, h + MARGIN_SIDES);


    }

    @Override
    public Collection<IEntry> getChildren() {
        return entries;
    }

    @Override
    public Section addChildren(IEntry widget) {
        entries.add(widget);
        reflow();
        return this;
    }

    @Override
    public Section addChildren(Collection<IEntry> widgets) {
        entries.addAll(widgets);
        reflow();
        return this;
    }

    @Override
    public ActionMenu getWindow() {
        return (ActionMenu) super.getWindow();
    }
}
