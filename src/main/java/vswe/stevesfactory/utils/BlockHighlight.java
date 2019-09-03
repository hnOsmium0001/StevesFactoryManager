package vswe.stevesfactory.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.List;

public final class BlockHighlight {

    private static List<BlockHighlight> highlights = new ArrayList<>();

    public static void createHighlight(BlockPos pos, int expire) {
        long expireTime = Minecraft.getInstance().world.getGameTime() + expire;
        highlights.add(new BlockHighlight(pos, expireTime));
    }

    public static void renderWorld(RenderWorldLastEvent event) {
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

    public void render(float particleTicks) {
        RenderingHelper.renderOutline(pos, particleTicks);
    }

    public boolean isExpired() {
        return Minecraft.getInstance().world.getGameTime() > expireTime;
    }
}
