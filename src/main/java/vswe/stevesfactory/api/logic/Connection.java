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
    private int fromIdx;
    private IProcedure to;
    private int toIdx;

    Connection(IProcedure from, int fromIdx, IProcedure to, int toIdx) {
        this.from = from;
        this.fromIdx = fromIdx;
        this.to = to;
        this.toIdx = toIdx;
    }

    public void remove() {
        from.removeOutputConnection(fromIdx);
        to.removeInputConnection(toIdx);
    }

    public IProcedure getSource() {
        return from;
    }

    public IProcedure getDestination() {
        return to;
    }

    public int getSourceOutputIndex() {
        return fromIdx;
    }

    public int getDestinationInputIndex() {
        return toIdx;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("fromOut", fromIdx)
                .add("to", to)
                .add("toIn", toIdx)
                .toString();
    }
}
