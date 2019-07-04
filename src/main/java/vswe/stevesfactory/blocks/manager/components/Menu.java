package vswe.stevesfactory.blocks.manager.components;

import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;

public abstract class Menu extends AbstractWidget {

    public static final TextureWrapper HEADING_BOX = TextureWrapper.ofFlowComponent(67, 154, 120, 13);

    public enum State {
        EXPANDED {
            @Override
            public int calculateHeight(int contentHeight) {
                return contentHeight + HEADING_BOX.getPortionHeight();
            }
        },
        COLLAPSED {
            @Override
            public int calculateHeight(int contentHeight) {
                return HEADING_BOX.getPortionHeight();
            }
        };


        public abstract int calculateHeight(int contentHeight);

    }

    private State state = State.COLLAPSED;

    public Menu(int height) {
        super(HEADING_BOX.getPortionWidth(), HEADING_BOX.getPortionHeight());
    }

    public abstract String getHeadingText();
    // TODO all the things

}
