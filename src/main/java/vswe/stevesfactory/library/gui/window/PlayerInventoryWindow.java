package vswe.stevesfactory.library.gui.window;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.slot.*;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class PlayerInventoryWindow extends AbstractWindow implements IPopupWindow {

    private static final TextureWrapper CLOSE = TextureWrapper.ofGUITexture("textures/gui/close.png", 64, 64, 0, 0, 64, 64);

    private final List<IWidget> children;
    private boolean alive = true;

    public PlayerInventoryWindow() {
        this(0, 0, ItemSlot::new);
    }

    public PlayerInventoryWindow(int x, int y, Function<ItemStack, AbstractItemSlot> factory) {
        setPosition(x, y);

        PlayerInventory playerInventory = Minecraft.getInstance().player.inventory;
        ItemSlotPanel inventory = new ItemSlotPanel(9, 3, playerInventory.mainInventory.subList(9, playerInventory.mainInventory.size()), factory);
        inventory.setLocation(0, 8 + 2);
        ItemSlotPanel hotbar = new ItemSlotPanel(9, 1, playerInventory.mainInventory.subList(0, 9), factory);
        hotbar.setLocation(0, inventory.getY() + inventory.getHeight() + 4);
        AbstractIconButton close = new AbstractIconButton(0, 0, inventory.getWidth() - 8, 0) {
            @Override
            public TextureWrapper getTextureNormal() {
                return CLOSE;
            }

            @Override
            public TextureWrapper getTextureHovered() {
                return CLOSE;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                alive = false;
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        close.setDimensions(8, 8);
        children = ImmutableList.of(close, inventory, hotbar);

        setContents(inventory.getWidth(), 8 + 2 + inventory.getHeight() + hotbar.getHeight());
    }

    @Override
    public int getBorderSize() {
        return 4;
    }

    @Override
    public Dimension getContentDimensions() {
        return null;
    }

    @Override
    public List<? extends IWidget> getChildren() {
        return children;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        BackgroundRenderers.drawVanillaStyle(getX(), getY(), getWidth(), getHeight(), 0F);
        renderChildren(mouseX, mouseY, particleTicks);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean shouldDiscard() {
        return !alive;
    }
}
