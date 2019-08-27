package vswe.stevesfactory.api.logic;

public final class Connection {

    public static Connection connect(IProcedure from, int out, IProcedure to, int in) {
        Connection connection = new Connection(from, out, to, in);
        from.setOutputConnection(connection, out);
        to.setInputConnection(connection, in);
        return connection;
    }

    private IProcedure from;
    private int fromOut;
    private IProcedure to;
    private int toIn;

    Connection(IProcedure from, int fromOut, IProcedure to, int toIn) {
        this.from = from;
        this.fromOut = fromOut;
        this.to = to;
        this.toIn = toIn;
    }

    public void remove() {
        from.removeOutputConnection(fromOut);
        to.removeInputConnection(toIn);
    }

    public IProcedure getSource() {
        return from;
    }

    public IProcedure getDestination() {
        return to;
    }

    public int getSourceOutputIndex() {
        return fromOut;
    }

    public int getDestinationInputIndex() {
        return toIn;
    }
}
