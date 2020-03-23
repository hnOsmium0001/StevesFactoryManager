package vswe.stevesfactory.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class WorkingAreaRenderer<T extends TileEntity & IWorkingAreaProvider> extends TileEntityRenderer<T> {

    public static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState(
            "translucent_transparency",
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.enableAlphaTest();
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.disableAlphaTest();
            });

    private static RenderType createRenderType() {
        RenderType.State state = RenderType.State.getBuilder()
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .build(true);
        return RenderType.makeType("sfm_working_area", DefaultVertexFormats.POSITION_COLOR, GL_QUADS, 256, false, true, state);
    }

    private static final RenderType RENDER_TYPE = createRenderType();

    private static final int R = 0;
    private static final int G = 51;
    private static final int B = 204;
    private static final int A = 100;

    public WorkingAreaRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T tile, float partialTicks, @Nonnull MatrixStack ms, @Nonnull IRenderTypeBuffer typeBuffer, int combinedLightIn, int combinedOverlayIn) {
        if (!tile.isRendering()) {
            return;
        }

        // No need for matrix manipulation for now
//        ms.push();
        AxisAlignedBB aabb = tile.getWorkingArea().offset(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
        {
            IVertexBuilder buffer = typeBuffer.getBuffer(RENDER_TYPE);

            Matrix4f mat = ms.getLast().getMatrix();
            float minX = (float) aabb.minX;
            float minY = (float) aabb.minY;
            float minZ = (float) aabb.minZ;
            float maxY = (float) aabb.maxY;
            float maxX = (float) aabb.maxX;
            float maxZ = (float) aabb.maxZ;

            // North face
            buffer.pos(mat, minX, minY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, maxY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, maxY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, minY, minZ).color(R, G, B, A).endVertex();

            // South face
            buffer.pos(mat, minX, minY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, minY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, maxY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, maxY, maxZ).color(R, G, B, A).endVertex();

            // Down face
            buffer.pos(mat, minX, minY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, minY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, minY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, minY, maxZ).color(R, G, B, A).endVertex();

            // Up face
            buffer.pos(mat, minX, maxY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, maxY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, maxY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, maxY, minZ).color(R, G, B, A).endVertex();

            // West face
            buffer.pos(mat, minX, minY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, minY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, maxY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, minX, maxY, minZ).color(R, G, B, A).endVertex();

            // East face
            buffer.pos(mat, maxX, minY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, maxY, minZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, maxY, maxZ).color(R, G, B, A).endVertex();
            buffer.pos(mat, maxX, minY, maxZ).color(R, G, B, A).endVertex();
        }
        {
            IVertexBuilder buffer = typeBuffer.getBuffer(RenderType.getLines());
            // Outlines
            RenderSystem.lineWidth(2F);
            WorldRenderer.drawBoundingBox(ms, buffer, aabb, R / 255F, G / 255F, B / 255F, 1F);
        }
//        ms.pop();
    }
}
