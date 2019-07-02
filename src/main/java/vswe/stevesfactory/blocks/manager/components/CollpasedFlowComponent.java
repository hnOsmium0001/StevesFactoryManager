package vswe.stevesfactory.blocks.manager.components;

import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;

public class CollpasedFlowComponent extends FlowComponent {

    public static class ExpandButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(1, 21, 9, 10);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(10, 21, 9, 10);

        public ExpandButton() {
            super(55, 6, 9, 10);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovering() {
            return HOVERING;
        }

    }

    private static final TextureWrapper BACKGROUND = TextureWrapper.ofFlowComponent(0, 0, 64, 20);

    public CollpasedFlowComponent() {
        super(BACKGROUND);
    }

    @Override
    public TextureWrapper getBackgroundTexture() {
        return BACKGROUND;
    }

}
