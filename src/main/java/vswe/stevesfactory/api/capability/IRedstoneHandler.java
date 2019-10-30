package vswe.stevesfactory.api.capability;

import net.minecraft.util.IStringSerializable;

public interface IRedstoneHandler {

    int getSignal();

    void setSignal(int signal);

    boolean isStrong();

    boolean isWeak();

    void setType(Type type);

    enum Type implements IStringSerializable {
        WEAK("weak", false),
        STRONG("strong", true);

        public final String name;
        public final boolean strong;

        Type(String name, boolean strong) {
            this.name = name;
            this.strong = strong;
        }

        @Override
        public String getName() {
            return name;
        }

        public boolean isStrong() {
            return strong;
        }

        public boolean isWeak() {
            return !strong;
        }

        public static Type getByIndicator(boolean strong) {
            return strong ? STRONG : WEAK;
        }

        public static Type[] VALUES = values();
    }
}
