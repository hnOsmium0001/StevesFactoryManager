package vswe.stevesfactory.library.gui.widget;

import net.minecraft.util.math.MathHelper;
import vswe.stevesfactory.library.gui.widget.ValueField.ExceptionBasedValueField;

import java.util.function.Consumer;

public class NumberField<V extends Number> extends ExceptionBasedValueField<V> {

    public static NumberField<Double> doubleField(int width, int height) {
        NumberField<Double> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(Double::parseDouble, d -> Double.toString(d));
        return field;
    }

    public static NumberField<Double> doubleFieldEmptiable(int width, int height) {
        NumberField<Double> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? 0.0 : Double.parseDouble(s), d -> Double.toString(d));
        return field;
    }

    public static NumberField<Double> doubleFieldRanged(int width, int height, double defaultValue, double lowerBound, double upperBound) {
        NumberField<Double> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? defaultValue : MathHelper.clamp(Double.parseDouble(s), lowerBound, upperBound), i -> Double.toString(i));
        return field;
    }

    public static NumberField<Float> floatField(int width, int height) {
        NumberField<Float> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(Float::parseFloat, f -> Float.toString(f));
        return field;
    }

    public static NumberField<Float> floatFieldEmptiable(int width, int height) {
        NumberField<Float> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? 0F : Float.parseFloat(s), f -> Float.toString(f));
        return field;
    }

    public static NumberField<Float> floatFieldRanged(int width, int height, float defaultValue, float lowerBound, float upperBound) {
        NumberField<Float> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? defaultValue : MathHelper.clamp(Float.parseFloat(s), lowerBound, upperBound), f -> Float.toString(f));
        return field;
    }

    public static NumberField<Long> longField(int width, int height) {
        NumberField<Long> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(Long::parseLong, i -> Long.toString(i));
        return field;
    }

    public static NumberField<Long> longFieldEmptiable(int width, int height) {
        NumberField<Long> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? 0L : Long.parseLong(s), i -> Long.toString(i));
        return field;
    }

    public static NumberField<Long> longFieldRanged(int width, int height, long defaultValue, long lowerBound, long upperBound) {
        NumberField<Long> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? defaultValue : Math.max(Math.min(Long.parseLong(s), upperBound), lowerBound), i -> Long.toString(i));
        return field;
    }

    public static NumberField<Integer> integerField(int width, int height) {
        NumberField<Integer> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(Integer::parseInt, i -> Integer.toString(i));
        return field;
    }

    public static NumberField<Integer> integerFieldEmptiable(int width, int height) {
        NumberField<Integer> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? 0 : Integer.parseInt(s), i -> Integer.toString(i));
        return field;
    }

    public static NumberField<Integer> integerFieldRanged(int width, int height, int defaultValue, int lowerBound, int upperBound) {
        NumberField<Integer> field = new NumberField<>(0, 0, width, height);
        field.setValueFormat(s -> s.isEmpty() ? defaultValue : MathHelper.clamp(Integer.parseInt(s), lowerBound, upperBound), i -> Integer.toString(i));
        return field;
    }

    public Consumer<V> onValueUpdated = s -> {};

    public NumberField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    protected boolean updateText(String text) {
        boolean result = super.updateText(text);
        onValueUpdated.accept(getValue());
        return result;
    }
}
