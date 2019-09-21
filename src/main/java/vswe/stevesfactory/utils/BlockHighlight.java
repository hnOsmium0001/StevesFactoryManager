package vswe.stevesfactory.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.*;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glLineWidth;

@EventBusSubscriber(modid = StevesFactoryManager.MODID, value = Dist.CLIENT, bus = Bus.FORGE)
public final class BlockHighlight {

    private static List<BlockHighlight> highlights = new ArrayList<>();

    public static void createHighlight(BlockPos pos, int expire) {
        long expireTime = Minecraft.getInstance().world.getGameTime() + expire;
        highlights.add(new BlockHighlight(pos, expireTime));
    }

    @SubscribeEvent
    public static void renderWorldLast(RenderWorldLastEvent event) {
        float particleTicks = event.getPartialTicks();
        for (BlockHighlight highlight : highlights) {
            highlight.render(particleTicks);
        }
        highlights.removeIf(BlockHighlight::isExpired);
    }

    private final BlockPos pos;
    private final long expireTime;

    public BlockHighlight(BlockPos pos, long expireTime) {
        this.pos = pos;
        this.expireTime = expireTime;
    }

    // #renderOutline, #renderOutlines, and #blockOutlineVertices are adapted from McJtyLib
    public static void renderOutline(BlockPos c, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        ClientPlayerEntity p = mc.player;
        double eyeHeight = p.getEyeHeight();
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks + eyeHeight;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.color3f(1F, 0F, 0F);
        GlStateManager.lineWidth(3);
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float mx = c.getX();
        float my = c.getY();
        float mz = c.getZ();
        buffer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        blockOutlineVertices(buffer, mx, my, mz, 1F, 0F, 0F, 1F);

        tessellator.draw();

        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

    public static void renderOutlines(ClientPlayerEntity p, Set<BlockPos> coordinates, int r, int g, int b, float partialTicks) {
        double eyeHeight = p.getEyeHeight();
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks + eyeHeight;
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

        RenderHelper.disableStandardItemLighting();
        Minecraft.getInstance().gameRenderer.disableLightmap();
        GlStateManager.disableDepthTest();
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableAlphaTest();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        GlStateManager.translated(-doubleX, -doubleY, -doubleZ);

        renderOutlines(coordinates, r, g, b, 4);

        GlStateManager.popMatrix();

        Minecraft.getInstance().gameRenderer.enableLightmap();
        GlStateManager.enableTexture();
    }

    /**
     * This method expects the GL state matrix to be translated to relative player position already (player.lastTickPos + (player.pos -
     * player.lastTickPos) * partialTicks)
     */
    public static void renderOutlines(Set<BlockPos> coordinates, int r, int g, int b, int thickness) {
        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        glLineWidth(thickness);

        for (BlockPos coordinate : coordinates) {
            float x = coordinate.getX();
            float y = coordinate.getY();
            float z = coordinate.getZ();

            blockOutlineVertices(buffer, x, y, z, r / 255F, g / 255F, b / 255F, 1F);
        }
        tessellator.draw();
    }

    public static void blockOutlineVertices(BufferBuilder buffer, float mx, float my, float mz, float r, float g, float b, float a) {
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my + 1, mz + 1).color(r, g, b, a).endVertex();

        buffer.pos(mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.pos(mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.pos(mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
    }

    public void render(float particleTicks) {
        renderOutline(pos, particleTicks);
    }

    public boolean isExpired() {
        return Minecraft.getInstance().world.getGameTime() > expireTime;
    }
}
