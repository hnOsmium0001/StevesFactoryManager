package vswe.stevesfactory.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import static org.lwjgl.opengl.GL11.*;

public class WorkingAreaRenderer<T extends TileEntity & IWorkingAreaProvider> extends TileEntityRenderer<T> {

    private static final int R = 0;
    private static final int G = 51;
    private static final int B = 204;
    private static final int A = 100;

    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!tile.isRendering()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture();
        Minecraft.getInstance().gameRenderer.disableLightmap();
        GlStateManager.disableLighting();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color3f(1F, 1F, 1F);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        // Move bounding box to the origin for camera translation
        AxisAlignedBB aabb = tile.getWorkingArea().offset(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());

        // Box
        {
            buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            // North
            buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();

            // South
            buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();

            // Down
            buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();

            // Up
            buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();

            // West
            buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();

            // East
            buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();

            Tessellator.getInstance().draw();
        }
        // Outlines
        {
            glLineWidth(2F);
            buffer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(R, G, B, A).endVertex();
            buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(R, G, B, A).endVertex();

            Tessellator.getInstance().draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlphaTest();
        RenderHelper.enableStandardItemLighting();
        Minecraft.getInstance().gameRenderer.enableLightmap();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
}
