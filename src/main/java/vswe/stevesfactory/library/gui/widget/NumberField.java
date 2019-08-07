package vswe.stevesfactory.library.gui.widget;

import vswe.stevesfactory.library.gui.widget.ValueField.ExceptionBasedValueField;

import java.util.function.Function;

public class NumberField<V extends Number> extends ExceptionBasedValueField<V> {

    public static NumberField<Double> doubleField() {
        return doubleField(0, 0);
    }

    public static NumberField<Double> doubleField(int width, int height) {
        return new NumberField<Double>(0, 0, width, height)
                .setValueFormat(Double::parseDouble, d -> Double.toString(d));
    }

    public static NumberField<Float> floatField() {
        return floatField(0, 0);
    }

    public static NumberField<Float> floatField(int width, int height) {
        return new NumberField<Float>(0, 0, width, height)
                .setValueFormat(Float::parseFloat, f -> Float.toString(f));
    }

    public static NumberField<Long> longField() {
        return longField(0, 0);
    }

    public static NumberField<Long> longField(int width, int height) {
        return new NumberField<Long>(0, 0, width, height)
                .setValueFormat(Long::parseLong, i -> Long.toString(i));
    }

    public static NumberField<Integer> integerField() {
        return integerField(0, 0);
    }

    public static NumberField<Integer> integerField(int width, int height) {
        return new NumberField<Integer>(0, 0, width, height)
                .setValueFormat(Integer::parseInt, i -> Integer.toString(i));
    }

    public NumberField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public NumberField<V> setValueFormat(Function<String, V> parser, Function<V, String> stringifier) {
        super.setValueFormat(parser, stringifier);
        return this;
    }

    @Override
    public NumberField<V> setValue(V number) {
        super.setValue(number);
        return this;
    }

    @Override
    public NumberField<V> setEditable(boolean editable) {
        super.setEditable(editable);
        return this;
    }

    @Override
    public NumberField<V> setText(String text) {
        super.setText(text);
        return this;
    }

    @Override
    public NumberField<V> scrollToFront() {
        super.scrollToFront();
        return this;
    }

    @Override
    public NumberField<V> selectAll() {
        super.selectAll();
        return this;
    }

    @Override
    public NumberField<V> setSelection(int start, int end) {
        super.setSelection(start, end);
        return this;
    }

    @Override
    public NumberField<V> clearSelection() {
        super.clearSelection();
        return this;
    }

    @Override
    public NumberField<V> replaceSelectedRegion(String replacement) {
        super.replaceSelectedRegion(replacement);
        return this;
    }

    @Override
    public NumberField<V> setBackgroundStyle(IBackgroundRenderer backgroundStyle) {
        super.setBackgroundStyle(backgroundStyle);
        return this;
    }
}
