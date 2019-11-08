package vswe.stevesfactory.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class WorkingAreaRenderer<T extends TileEntity & IWorkingAreaProvider> extends TileEntityRenderer<T> {

    @Override
    public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!tile.isRendering()) {
            return;
        }

        int r = 0;
        int g = 204;
        int b = 255;
        int a = 100;

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        GlStateManager.color4f(r / 255F, g / 255F, b / 255F, a / 255F);
        GlStateManager.disableCull();
        GlStateManager.disableTexture();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        AxisAlignedBB aabb = tile.getWorkingArea().offset(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());

        // North
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();

        // South
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();

        // Down
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();

        // Up
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();

        // West
        buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();

        // East
        buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.disableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
