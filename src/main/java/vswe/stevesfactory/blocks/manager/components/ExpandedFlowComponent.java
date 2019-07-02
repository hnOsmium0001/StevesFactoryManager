package vswe.stevesfactory.blocks.manager.components;

import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;

public class ExpandedFlowComponent extends FlowComponent {

    public static class CollpaseButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(1, 31, 9, 10);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(10, 31, 9, 10);

        public CollpaseButton() {
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

    public static class RenameButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(33, 190, 9, 9);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(42, 190, 9, 9);

        public RenameButton() {
            super(44, 7, 9, 9);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled()) {
                setEnabled(false);
                return true;
            }
            return false;
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

    public static class SaveRenameButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(33, 199, 9, 9);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(40, 199, 9, 9);

        public SaveRenameButton() {
            super(46, 3, 7, 7);
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

    public static class CancelRenameButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(33, 206, 9, 9);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(40, 206, 9, 9);

        public CancelRenameButton() {
            super(46, 12, 7, 7);
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

    private static final TextureWrapper BACKGROUND = TextureWrapper.ofFlowComponent(63, 1, 124, 152);

    public ExpandedFlowComponent() {
        super(BACKGROUND);
    }

    @Override
    public TextureWrapper getBackgroundTexture() {
        return BACKGROUND;
    }

}
