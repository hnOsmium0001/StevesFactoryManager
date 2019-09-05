package vswe.stevesfactory.api.logic;

import com.google.common.base.MoreObjects;

public final class Connection {

    @SuppressWarnings("UnusedReturnValue")
    public static Connection create(IProcedure from, int out, IProcedure to, int in) {
        Connection connection = new Connection(from, out, to, in);
        from.setOutputConnection(connection, out);
        to.setInputConnection(connection, in);
        return connection;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Connection createAndOverride(IProcedure from, int out, IProcedure to, int in) {
        Connection successor = from.successors()[out];
        if (successor != null) {
            successor.remove();
        }

        Connection predecessor = to.predecessors()[in];
        if (predecessor != null) {
            predecessor.remove();
        }

        return create(from, out, to, in);
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("fromOut", fromOut)
                .add("to", to)
                .add("toIn", toIn)
                .toString();
    }
}
