package vswe.stevesfactory.api.logic;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    private static final ImmutableList<Point> EMPTY_LIST = ImmutableList.of();

    private IProcedure from;
    private int fromIdx;
    private IProcedure to;
    private int toIdx;
    private int toIn;
    private List<Point> polylineNodes = EMPTY_LIST;

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

    public void clearNodes() {
        if (polylineNodes != EMPTY_LIST) {
            polylineNodes.clear();
        }
    }

    private void updateListType() {
        if (polylineNodes == EMPTY_LIST) {
            polylineNodes = new ArrayList<>();
        }
    }

    public List<Point> getPolylineNodes() {
        updateListType();
        return polylineNodes;
    }

    long[] toPolylineData() {
        long[] data = new long[polylineNodes.size()];
        for (int i = 0; i < polylineNodes.size(); i++) {
            Point point = polylineNodes.get(i);
            data[i] = (long) point.x << 32 | point.y;
        }
        return data;
    }

    void fromPolylineData(long[] data) {
        updateListType();
        polylineNodes.clear();
        for (long pos : data) {
            polylineNodes.add(new Point((int) (pos >> 32), (int) pos));
        }
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
