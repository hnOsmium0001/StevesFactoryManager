package vswe.stevesfactory.api.capability;

import net.minecraft.util.text.ITextComponent;

import java.util.*;
import java.util.stream.Stream;

public class TextDocument implements ITextDocument {

    private List<ITextComponent> lines = new ArrayList<>();

    @Override
    public int getLines() {
        return lines.size();
    }

    @Override
    public ITextComponent getLine(int line) {
        return lines.get(line);
    }

    @Override
    public void setLine(int line, ITextComponent text) {
        lines.set(line, text);
    }

    @Override
    public void addLine(ITextComponent line) {
        lines.add(line);
    }

    @Override
    public List<ITextComponent> view() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    public Stream<ITextComponent> stream() {
        return lines.stream();
    }

    @Override
    public Stream<String> textStream() {
        return lines.stream().map(ITextComponent::getFormattedText);
    }
}
