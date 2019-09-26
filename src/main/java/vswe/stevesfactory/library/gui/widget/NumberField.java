package vswe.stevesfactory.library.gui.widget;

import net.minecraft.util.math.MathHelper;
import vswe.stevesfactory.library.gui.widget.ValueField.ExceptionBasedValueField;

import java.util.function.Function;

public class NumberField<V extends Number> extends ExceptionBasedValueField<V> {

    public static NumberField<Double> doubleField(int width, int height) {
        return new NumberField<Double>(0, 0, width, height)
                .setValueFormat(Double::parseDouble, d -> Double.toString(d));
    }

    public static NumberField<Double> doubleFieldEmptiable(int width, int height) {
        return new NumberField<Double>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? 0.0 : Double.parseDouble(s), d -> Double.toString(d));
    }

    public static NumberField<Double> doubleFieldRanged(int width, int height, double defaultValue, double lowerBound, double upperBound) {
        return new NumberField<Double>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? defaultValue : MathHelper.clamp(Double.parseDouble(s), lowerBound, upperBound), i -> Double.toString(i));
    }

    public static NumberField<Float> floatField(int width, int height) {
        return new NumberField<Float>(0, 0, width, height)
                .setValueFormat(Float::parseFloat, f -> Float.toString(f));
    }

    public static NumberField<Float> floatFieldEmptiable(int width, int height) {
        return new NumberField<Float>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? 0F : Float.parseFloat(s), f -> Float.toString(f));
    }

    public static NumberField<Float> floatFieldRanged(int width, int height, float defaultValue, float lowerBound, float upperBound) {
        return new NumberField<Float>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? defaultValue : MathHelper.clamp(Float.parseFloat(s), lowerBound, upperBound), i -> Float.toString(i));
    }

    public static NumberField<Long> longField(int width, int height) {
        return new NumberField<Long>(0, 0, width, height)
                .setValueFormat(Long::parseLong, i -> Long.toString(i));
    }

    public static NumberField<Long> longFieldEmptiable(int width, int height) {
        return new NumberField<Long>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? 0L : Long.parseLong(s), i -> Long.toString(i));
    }

    public static NumberField<Long> longFieldRanged(int width, int height, long defaultValue, long lowerBound, long upperBound) {
        return new NumberField<Long>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? defaultValue : Math.max(Math.min(Long.parseLong(s), upperBound), lowerBound), i -> Long.toString(i));
    }

    public static NumberField<Integer> integerField(int width, int height) {
        return new NumberField<Integer>(0, 0, width, height)
                .setValueFormat(Integer::parseInt, i -> Integer.toString(i));
    }

    public static NumberField<Integer> integerFieldEmptiable(int width, int height) {
        return new NumberField<Integer>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? 0 : Integer.parseInt(s), i -> Integer.toString(i));
    }

    public static NumberField<Integer> integerFieldRanged(int width, int height, int defaultValue, int lowerBound, int upperBound) {
        return new NumberField<Integer>(0, 0, width, height)
                .setValueFormat(s -> s.isEmpty() ? defaultValue : MathHelper.clamp(Integer.parseInt(s), lowerBound, upperBound), i -> Integer.toString(i));
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
}
