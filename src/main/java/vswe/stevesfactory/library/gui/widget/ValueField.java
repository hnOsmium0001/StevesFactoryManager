package vswe.stevesfactory.library.gui.widget;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ValueField<V> extends TextField {

    public static <V> ExceptionBasedValueField<V> exceptionBasedValueField(int width, int height) {
        return exceptionBasedValueField(0, 0, width, height);
    }

    public static <V> ExceptionBasedValueField<V> exceptionBasedValueField(int x, int y, int width, int height) {
        return new ExceptionBasedValueField<>(x, y, width, height);
    }

    public static class ExceptionBasedValueField<V> extends ValueField<V> {

        public ExceptionBasedValueField(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        protected boolean updateText(String text) {
            try {
                value = getParser().apply(text);
                setInternalText(text);
                return true;
            } catch (RuntimeException ignored) {
            }
            return false;
        }

        @Override
        public void onFocusChanged(boolean focus) {
            // On loss focus, we override the text with number
            // This is for special format limiting that does not throw an exception
            if (!focus) {
                setText(getStringifier().apply(value));
            }
        }
    }

    public static <V> ValidatorBasedValueField<V> validatorBasedValueField(int width, int height) {
        return validatorBasedValueField(0, 0, width, height);
    }

    public static <V> ValidatorBasedValueField<V> validatorBasedValueField(int x, int y, int width, int height) {
        return new ValidatorBasedValueField<>(x, y, width, height);
    }

    public static class ValidatorBasedValueField<V> extends ValueField<V> {

        private Predicate<String> formatValidator;

        public ValidatorBasedValueField(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        protected boolean updateText(String text) {
            if (formatValidator.test(text)) {
                setInternalText(text);
                this.value = getParser().apply(getText());
                return true;
            }
            return false;
        }

        @Deprecated
        @Override
        public void setValueFormat(Function<String, V> parser, Function<V, String> stringifier) {
            throw new UnsupportedOperationException();
        }

        public void setValueFormat(Predicate<String> formatValidator, Function<String, V> parser, Function<V, String> stringifier) {
            this.formatValidator = formatValidator;
            super.setValueFormat(parser, stringifier);
        }
    }

    private Function<String, V> parser;
    private Function<V, String> stringifier;
    protected V value;

    protected ValueField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void setValueFormat(Function<String, V> parser, Function<V, String> stringifier) {
        this.parser = parser;
        this.stringifier = stringifier;
    }

    @Override
    protected abstract boolean updateText(String text);

    protected final void setInternalText(String text) {
        super.updateText(text);
    }

    public V getValue() {
        return value;
    }

    public void setValue(V number) {
        this.value = number;
        super.updateText(stringifier.apply(number));
    }

    public Function<String, V> getParser() {
        return parser;
    }

    public Function<V, String> getStringifier() {
        return stringifier;
    }
}
