package vswe.stevesfactory.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, value = Dist.CLIENT, bus = Bus.FORGE)
public final class BlockHighlight {

    private static List<BlockHighlight> highlights = new ArrayList<>();

    public static void createHighlight(BlockPos pos, int expire) {
        long expireTime = Minecraft.getInstance().world.getGameTime() + expire;
        highlights.add(new BlockHighlight(pos, expireTime));
    }

    @SubscribeEvent
    public static void renderWorldLast(RenderWorldLastEvent event) {
        for (BlockHighlight highlight : highlights) {
            highlight.render();
        }
        highlights.removeIf(BlockHighlight::isExpired);
    }

    private final BlockPos pos;
    private final long expireTime;

    public BlockHighlight(BlockPos pos, long expireTime) {
        this.pos = pos;
        this.expireTime = expireTime;
    }

    public void render() {
        renderOutline(pos);
    }

    public boolean isExpired() {
        return Minecraft.getInstance().world.getGameTime() > expireTime;
    }

    public static void renderOutline(BlockPos c) {
        GlStateManager.pushMatrix();
        Vec3d vpos = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        GlStateManager.translated(-vpos.x, -vpos.y, -vpos.z);
        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(3);

        float mx = c.getX();
        float my = c.getY();
        float mz = c.getZ();
        WorldRenderer.drawBoundingBox(mx, my, mz, mx + 1, my + 1, mz + 1, 1F, 0F, 0F, 1F);

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}
