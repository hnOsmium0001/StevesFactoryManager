package vswe.stevesfactory.api.capability;

import net.minecraft.util.text.ITextComponent;

import java.util.Collection;
import java.util.stream.Stream;

public interface ITextDocument {

    int getLines();

    ITextComponent getLine(int line);

    void setLine(int line, ITextComponent text);

    void addLine(ITextComponent line);

    Collection<ITextComponent> view();

    Stream<ITextComponent> stream();

    Stream<String> textStream();
}
