package vswe.stevesfactory.library.gui.background;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.StevesFactoryManager;

import java.awt.*;
import java.util.concurrent.ExecutionException;

@OnlyIn(Dist.CLIENT)
public final class DisplayListCaches {

    private DisplayListCaches() {
    }

    private static final Cache<Rectangle, Integer> VANILLA_BACKGROUND_CACHE = CacheBuilder.newBuilder()
            .removalListener(removal -> GLAllocation.deleteDisplayLists((Integer) removal.getValue()))
            .build();

    public static int createVanillaStyleBackground(Rectangle rectangle) {
        try {
            return VANILLA_BACKGROUND_CACHE.get(rectangle, () -> {
                int id = GLAllocation.generateDisplayLists(1);
                GlStateManager.newList(id, GL11.GL_COMPILE);
                {
                    BackgroundRenderer.drawVanillaStyle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
                GlStateManager.endList();
                return id;
            });
        } catch (ExecutionException e) {
            StevesFactoryManager.logger.error("Exception when creating OpenGL display list for {} for vanilla-style background", rectangle, e);
            return -1;
        }
    }

    public static int createVanillaStyleBackground(int x, int y, int width, int height) {
        return createVanillaStyleBackground(new Rectangle(x, y, width, height));
    }

}
